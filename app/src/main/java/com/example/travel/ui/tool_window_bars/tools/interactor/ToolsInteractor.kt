package com.example.travel.ui.tool_window_bars.tools.interactor

import com.example.travel.data.database.elevations.ElevationsRepo
import com.example.travel.data.network.ElevationDataSource
import com.example.travel.data.database.elevations.LinearCoordinate
import com.example.travel.data.database.elevations.drawBresenhamLine
import com.example.travel.model.map.MapPoint
import com.example.travel.ui.base.interactor.BaseInteractor
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class ToolsInteractor @Inject constructor(private val elevationsRepo: ElevationsRepo, private val elevationDataSource: ElevationDataSource) : BaseInteractor(), ToolsMvpInteractor {
    override fun seedElevationBox(): Observable<ElevationBox> = elevationsRepo.seedElevationBox()

    override fun seedDownloadQueue() = elevationDataSource.getDownloadQueue()

    override fun seedDownloadProgress(elevationBox: ElevationBox): Observable<Int> {
        elevationsRepo.elevationBox = ElevationBox(0.0,0.0,0.0,0.0,0,0)

        val squareXFinal = if (elevationBox.columns % ElevationBox.Size == 0) elevationBox.columns / ElevationBox.Size else 1 + elevationBox.columns / ElevationBox.Size
        val squareYFinal = if (elevationBox.rows % ElevationBox.Size == 0) elevationBox.rows / ElevationBox.Size else 1 + elevationBox.rows / ElevationBox.Size

        var squareNumber = 0

        var result = elevationsRepo.clear().andThen(Observable.just(-(squareXFinal * squareYFinal)))


        for (iterY in 0 until elevationBox.rows step ElevationBox.Size) {
            val latitudeStop = elevationBox.latitudeNorth - elevationBox.latitudeStep * iterY
            val latitudeStart = latitudeStop - elevationBox.latitudeStep * (ElevationBox.Size - 1)

            for (iterX in 0 until elevationBox.columns step ElevationBox.Size) {
                val longitudeStart = elevationBox.longitudeWest + elevationBox.longitudeStep * (iterX + 1)
                val longitudeStop = longitudeStart + elevationBox.longitudeStep * (ElevationBox.Size - 1)

                val request = elevationDataSource.performRequest(latitudeStart, longitudeStart, latitudeStop, longitudeStop)
                        .flatMap { elevationsList: List<Int> ->
                            elevationsRepo.addElevations(squareNumber++, elevationsList).andThen(Observable.just(squareNumber))
                        }

                result = Observable.concat(result, request)
            }
        }

        return result.doOnComplete { elevationsRepo.elevationBox = elevationBox}
    }


    override fun getElevations(startGeoPoint: MapPoint, finishGeoPoint: MapPoint): Single<List<Int>> {
        val startCoordinate = getCoordinate(startGeoPoint.latitude, startGeoPoint.longitude)
        val finishCoordinate = getCoordinate(finishGeoPoint.latitude, finishGeoPoint.longitude)

        if (startCoordinate != null && finishCoordinate != null) {
            val linePoints = drawBresenhamLine(startCoordinate, finishCoordinate)

            return elevationsRepo.getElevations(linePoints)
        }

        return Single.fromCallable { LinkedList<Int>() }
    }

    private fun getCoordinate(latitude: Double, longitude: Double): LinearCoordinate<Int, Int>? {
        val box = elevationsRepo.elevationBox

        box?.let {
            val latitudeNorth = box.latitudeNorth
            val latitudeStep = box.latitudeStep
            val longitudeWest = box.longitudeWest
            val longitudeStep = box.longitudeStep
            val rows = box.rows
            val columns = box.columns

            val latitudeSouth = latitudeNorth - latitudeStep * rows
            val longitudeEast = longitudeWest + longitudeStep * columns

            if (latitude > latitudeNorth || latitude < latitudeSouth) {
                println(String.format(Locale.ENGLISH, "ERROR! (latitude > latitudeNorth) || (latitude < latitudeSouth); latitude(%.7f), latitudeNorth(%.7f), latitudeSouth(%.7f)", latitude, latitudeNorth, latitudeSouth))

                return null
            }

            if (longitude > longitudeEast || longitude < longitudeWest) {
                println(String.format(Locale.ENGLISH, "ERROR! (longitude > longitudeEast) || (longitude < longitudeWest); + latitude(%.7f), latitudeNorth(%.7f), latitudeSouth(%.7f)", latitude, latitudeNorth, latitudeSouth))

                return null
            }

            val ystep = (latitudeNorth - latitude) / latitudeStep
            val xstep = (longitude - longitudeWest) / longitudeStep

            var xasis = (xstep * 10).toInt()
            xasis = if (xasis % 10 >= 5) xasis / 10 + 1 else xasis / 10

            var yasis = (ystep * 10).toInt()
            yasis = if (yasis % 10 >= 5) yasis / 10 + 1 else yasis / 10

            return LinearCoordinate<Int, Int>(xasis, yasis)
        }

        return null
    }
}