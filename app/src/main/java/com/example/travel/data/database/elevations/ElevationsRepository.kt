package com.example.travel.data.database.elevations

import com.example.travel.data.database.AppDatabase
import com.example.travel.data.preferences.RectangleParametersStorage
import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject

class ElevationsRepository @Inject constructor(private val appDatabase: AppDatabase, private val rectangleParametersStorage: RectangleParametersStorage) : ElevationsRepo {

    companion object {
        private val TAG = ElevationsRepository::class.java.simpleName
    }

    private val elevationBoxSubject: BehaviorSubject<ElevationBox> = BehaviorSubject.create()

    override var elevationBox
        get() = elevationBoxSubject.value
        set(value) {
            value?.let {
                elevationBoxSubject.onNext(it)

                rectangleParametersStorage.elevationBox = it
            }
        }

    init {
        elevationBox = rectangleParametersStorage.elevationBox
    }

    override fun seedElevationBox(): Observable<ElevationBox> = elevationBoxSubject

    override fun clear(): Completable {
        return appDatabase.elevationDao().clearTable()
    }

    override fun addElevations(squareNumber: Int, elevationsList: List<Int>): Completable {
        val baseAddress = squareNumber * ElevationBox.Size * ElevationBox.Size

        var pointNumber = 0

        val elevationList = LinkedList<Elevation>()

        for (iterY in ElevationBox.Size - 1 downTo 0) {
            for (iterX in 0 until ElevationBox.Size) {
                val elevation = Elevation()
                elevation.pointNumber = (baseAddress + pointNumber++).toLong()
                elevation.elevation = elevationsList[iterX + iterY * ElevationBox.Size]

                elevationList.add(elevation)
            }
        }

        return appDatabase.elevationDao().insert(elevationList)

    }

    override fun getElevations(linePoints: List<LinearCoordinate<Int, Int>>): Single<List<Int>> {
        val pointNumbers = ArrayList<Long>()

        elevationBox?.let {
            for (pair in linePoints) {
                val squareNumber = pair.y / ElevationBox.Size * (it.columns / ElevationBox.Size) + pair.x / ElevationBox.Size

                val pointNumber = (squareNumber * ElevationBox.Size * ElevationBox.Size +
                        pair.y % ElevationBox.Size * ElevationBox.Size +
                        pair.x % ElevationBox.Size).toLong()

                pointNumbers.add(pointNumber)
            }
        }

        return appDatabase.elevationDao().getByPointNumbers(pointNumbers).flatMap{elevationsList: List<Elevation> ->
            val elevations = ArrayList<Int>()

            val elevationsMap = elevationsList.map { it.pointNumber to it.elevation }.toMap()

            for (pointNumber in pointNumbers) {
                elevations.add(elevationsMap.getValue(pointNumber))
            }

            Single.just<List<Int>>(elevations)
        }
    }
}