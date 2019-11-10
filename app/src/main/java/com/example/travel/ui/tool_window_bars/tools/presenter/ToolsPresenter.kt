package com.example.travel.ui.tool_window_bars.tools.presenter

import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.travel.R
import com.example.travel.model.map.MapModel
import com.example.travel.model.map.MapPoint
import com.example.travel.ui.base.presenter.BasePresenter
import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import com.example.travel.ui.tool_window_bars.tools.interactor.ToolsMvpInteractor
import com.example.travel.ui.tool_window_bars.tools.view.ToolsMvpView
import com.example.travel.util.rx.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

class ToolsPresenter<V : ToolsMvpView, I: ToolsMvpInteractor> @Inject
            internal constructor(interactor: I,
                                 schedulerProvider: SchedulerProvider,
                                 compositeDisposable: CompositeDisposable,
                                 private var mapModel: MapModel) :
        BasePresenter<V, I>(interactor, schedulerProvider, compositeDisposable), ToolsMvpPresenter<V> {

    companion object {
        private val TAG = ToolsPresenter::class.java.simpleName
        private const val LINE_WIDTH = 2
        private const val INIT = 0
        private const val ADD_POINT = 1
    }

    private var profileLineId: Long = -1
    private var elevationLine: Long = -1
    private val markers: MutableList<PointInfo> = ArrayList()
    private val ways: MutableList<WayInfo> = ArrayList()
    private var mLastAdditionalState = INIT
    private var additionalState = INIT

    data class PointInfo(var position: MapPoint,
                         var markerId: Long = 0,
                         var source: WayInfo? = null,
                         var destination: WayInfo? = null)

    class WayInfo(var startGeoPoint: MapPoint, var finishGeoPoint: MapPoint) {
        var distance: Double = 0.0
        var elevations: List<Int>? = null

        fun updateStartGeoPoint(start: MapPoint) {
            startGeoPoint = start

            update()
        }

        fun updateFinishGeoPoint(finish: MapPoint) {
            finishGeoPoint = finish

            update()
        }

        fun update() {
            elevations = null

            val dis = FloatArray(1)
            Location.distanceBetween(startGeoPoint.latitude, startGeoPoint.longitude,
                    finishGeoPoint.latitude, finishGeoPoint.longitude, dis)

            distance = dis[0].toDouble()
        }
    }

    override fun onViewInitialized() {
        compositeDisposable.add(mapModel.mapClickEvents
                .observeOn(schedulerProvider.ui())
                .subscribe { onMapClick(it) }
        )

        compositeDisposable.add(mapModel.markerDragListener
                .observeOn(schedulerProvider.ui())
                .filter { pair ->
                    null != markers.find { it.markerId == pair.first}
                }
                .subscribe { pair -> subscribeMarkerDrag(pair.first, pair.second) }
        )

        interactor?.let {
            compositeDisposable.add(it.seedElevationBox()
                .observeOn(schedulerProvider.ui())
                .subscribe { onNewElevationBox(it) }
            )

            compositeDisposable.add(it.seedDownloadQueue()
                    .subscribe { elevationBox: ElevationBox ->
                        subscribeProgress(it.seedDownloadProgress(elevationBox))
                    }
            )
        }
    }

    private fun onNewElevationBox(elevationBox: ElevationBox) {
        val mapPoints = ArrayList<MapPoint>()

        if (elevationBox.rows > 0 && elevationBox.columns > 0) {
            val latitudeSouth = elevationBox.latitudeNorth - elevationBox.latitudeStep * elevationBox.rows
            val longitudeEast = elevationBox.longitudeWest + elevationBox.longitudeStep * elevationBox.columns

            mapPoints.apply {
                add(MapPoint(elevationBox.latitudeNorth, elevationBox.longitudeWest))
                add(MapPoint(elevationBox.latitudeNorth, longitudeEast))
                add(MapPoint(latitudeSouth, longitudeEast))
                add(MapPoint(latitudeSouth, elevationBox.longitudeWest))
                add(MapPoint(elevationBox.latitudeNorth, elevationBox.longitudeWest))
            }
        }

        if (elevationLine == -1L) {
            elevationLine = mapModel.addPolyline(R.color.elevationLine, LINE_WIDTH.toFloat())
        }

        mapModel.updatePolylineMapPoints(elevationLine, mapPoints)
    }

    private fun subscribeProgress(source: Observable<Int>) {
        compositeDisposable.add(source
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : Consumer<Int> {
                    var squareNumberFinal = 0

                    @Throws(Exception::class)
                    override fun accept(int: Int) {
                        var value = int
                        if (value < 0) {
                            squareNumberFinal = (-value)
                        } else {
                            value++
                            getView()?.updateProgressDownload(String.format(Locale.ENGLISH, "Elevations: %d/%d", value, squareNumberFinal), value, squareNumberFinal)
                        }
                    }
                }, Consumer { throwable ->

                    Log.e(TAG, "onError: $throwable")

                    throwable.printStackTrace()

                    getView()?.changeProgressDownload(R.string.tools_tab_error)
                }, Action { getView()?.changeProgressDownload(R.string.tools_tab_finish) }))

        getView()?.showProgressDownload()
    }

    private fun subscribeMarkerDrag(id: Long, source: Observable<MapPoint>) {
        val pointInfo: PointInfo? = markers.find { it.markerId == id }

        compositeDisposable.add(source
                .observeOn(schedulerProvider.ui())
                .subscribe({ mapPoint ->
                            pointInfo?.let {
                                it.position = mapPoint
                                it.source?.updateFinishGeoPoint(it.position)
                                it.destination?.updateStartGeoPoint(it.position)

                                measurementDistanceChanged()

                                updateWay()
                            }
                        },
                        { throwable -> Log.e(TAG, throwable.message) },
                        {
                            pointInfo?.let {pointInfoIt: PointInfo ->
                                pointInfoIt.source?.let {
                                    it.updateFinishGeoPoint(pointInfoIt.position)

                                    updateElevations(it)
                                }
                                pointInfoIt.destination?.let {
                                    it.updateStartGeoPoint(pointInfoIt.position)

                                    updateElevations(it)
                                }

                                var distance = 0f

                                for (pointInfoLocal in markers) {
                                    mapModel.updateMarkerTitle(pointInfoLocal.markerId, String.format(Locale.ENGLISH, "%.1f, m\n%.07f °\n%.07f °", distance, pointInfoLocal.position.latitude, pointInfoLocal.position.longitude))

                                    pointInfoLocal.destination?.let {
                                        distance += it.distance.toFloat()
                                    }
                                }

                                updateWay()

                                mapModel.showInfoWindow(pointInfoIt.markerId)
                            }
                        })
        )
    }

    override fun onAddButtonClick() {
        if (additionalState == INIT) {
            additionalState = ADD_POINT

            getView()?.setAddButtonColor(Color.GREEN)
        } else if (additionalState == ADD_POINT) {
            additionalState = INIT

            getView()?.setAddButtonColor(Color.TRANSPARENT)
        }
    }

    override fun onClearButtonClick() {
        getView()?.clearProfileElevation()

        clear()
    }

    override fun buttonDownloadClick() {
    //    if (elevationModel.ready) {
            getView()?.showSelectLevelsDialog()
//        } else {
//            getView()?.showCancelDownloadDialog()
//        }
    }

    override fun onConfirmCancel() {
        mapModel.cancelAllJobs()
    }

    override fun resume() {
        additionalState = mLastAdditionalState

        setVisibility(true)
    }

    override fun pause() {
        mLastAdditionalState = additionalState

        additionalState = INIT

        setVisibility(false)
    }

    override fun onDetach() {
        for (pointInfo in markers) {
            mapModel.removeMarker(pointInfo.markerId)
        }

        if (profileLineId != -1L) {
            mapModel.removePolyline(profileLineId)

            profileLineId = -1
        }

        super.onDetach()
    }

    private fun setVisibility(visibility: Boolean) {
        for (pointInfo in markers) {
            mapModel.setMarkerVisibility(pointInfo.markerId, visibility)
        }

        if (profileLineId != -1L) {
            mapModel.setPolylineVisibility(profileLineId, visibility)

            mapModel.invalidateMapView()
        }
    }

    private fun onMapClick(mapPoint: MapPoint) {
        if (additionalState == ADD_POINT) {
            if (markers.isEmpty()) {
                profileLineId = mapModel.addPolyline(R.color.profileLine, LINE_WIDTH.toFloat())
            }

            val pointInfo = PointInfo(mapPoint)

            pointInfo.markerId = mapModel.addMarker(mapPoint)

            mapModel.setMarkerDraggable(pointInfo.markerId, true)

            var distance = 0f

            if (markers.isNotEmpty()) {
                val startPointInfo = markers.last()
                val finishGPointInfo = pointInfo

                val wayInfo = WayInfo(startPointInfo.position,
                        mapPoint)

                startPointInfo.destination = wayInfo
                finishGPointInfo.source = wayInfo

                wayInfo.update()

                ways.add(wayInfo)

                distance = measurementDistanceChanged()

                updateElevations(wayInfo)
            }

            markers.add(pointInfo)

            mapModel.updateMarkerTitle(pointInfo.markerId, String.format(Locale.ENGLISH, "%.1f, m\n%.07f °\n%.07f °", distance, mapPoint.latitude, mapPoint.longitude))

            updateWay()
        }
    }

    private fun clear() {
        for (pointInfo in markers) {
            mapModel.removeMarker(pointInfo.markerId)
        }

        if (profileLineId != -1L) {
            mapModel.removePolyline(profileLineId)

            profileLineId = -1L
        }

        markers.clear()
        ways.clear()

        mapModel.invalidateMapView()

        measurementDistanceChanged()
    }

    private fun measurementDistanceChanged(): Float {
        var distance = 0f

        for (way in ways) {
            distance += way.distance.toFloat()
        }

        getView()?.updateDistance(String.format(Locale.ENGLISH, "%.1f, m", distance))

        return distance
    }

    private fun updateWay() {
        val mapPoints = ArrayList<MapPoint>()

        for (pointInfo in markers) {
            mapPoints.add(pointInfo.position)
        }

        if (profileLineId != -1L) {
            mapModel.updatePolylineMapPoints(profileLineId, mapPoints)
            mapModel.invalidateMapView()
        }
    }

    private fun updateElevations(wayInfo: WayInfo) {

        interactor?.let {
            compositeDisposable.add(
                    it.getElevations(wayInfo.startGeoPoint, wayInfo.finishGeoPoint)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({ elevations ->
                        wayInfo.elevations = elevations

                        getView()?.clearProfileElevation()

                        getView()?.changeProfileElevation(ways)
                    }, { throwable ->

                        Log.e(TAG, "onError: $throwable")

                        throwable.printStackTrace()
                    })
        )}
    }
}
