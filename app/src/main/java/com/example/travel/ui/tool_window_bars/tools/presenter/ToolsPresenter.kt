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
    private var lastAdditionalState = INIT
    private var additionalState = INIT

    data class PointInfo(var position: MapPoint,
                         var markerId: Long = 0,
                         var source: WayInfo? = null,
                         var destination: WayInfo? = null)

    class WayInfo(var startGeoPoint: MapPoint, var finishGeoPoint: MapPoint) {
        var distance: Float = 0F
        var elevations: List<Int>? = null

        init {
            calculateDistance()
        }

        fun updateStartGeoPoint(start: MapPoint) {
            startGeoPoint = start

            calculateDistance()
        }

        fun updateFinishGeoPoint(finish: MapPoint) {
            finishGeoPoint = finish

            calculateDistance()
        }

        private fun calculateDistance() {
            elevations = null

            val dis = FloatArray(1)
            Location.distanceBetween(startGeoPoint.latitude, startGeoPoint.longitude,
                    finishGeoPoint.latitude, finishGeoPoint.longitude, dis)

            distance = dis[0]
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
                .subscribe { elevationBox: ElevationBox ->onNewElevationBox(elevationBox) }
            )

            compositeDisposable.add(it.seedDownloadQueue()
                    .observeOn(schedulerProvider.io())
                    .flatMap { elevationBox: ElevationBox -> it.seedDownloadProgress(elevationBox) }
                    .observeOn(schedulerProvider.ui())
                    .subscribe(object : Consumer<Int> {
                        var squareNumberFinal = 0

                        @Throws(Exception::class)
                        override fun accept(value: Int) {
                            if (value < 0) {
                                squareNumberFinal = (-value)
                                getView()?.showProgressDownload()
                            } else {
                                getView()?.updateProgressDownload(String.format(Locale.ENGLISH, "Elevations: %d/%d", value, squareNumberFinal), value, squareNumberFinal)
                            }

                            if (value == squareNumberFinal) {
                                   getView()?.changeProgressDownload(R.string.tools_tab_finish)
                            }
                        }
                    }, Consumer { throwable ->

                        Log.e(TAG, "onError: $throwable")

                        throwable.printStackTrace()

                        getView()?.changeProgressDownload(R.string.tools_tab_error)
                    })
            )
        }

        // Thread test
        interactor?.let {
            compositeDisposable.add(it.seedDownloadQueue()
                    .observeOn(schedulerProvider.io())
                    .flatMap { elevationBox: ElevationBox ->
                        Log.i(TAG, "test: elevationBox: $elevationBox")
                        Log.i(TAG, "test: Thread.currentThread().name: ${Thread.currentThread().name}")

                        Observable.just(elevationBox)
                    }
                    .observeOn(schedulerProvider.ui())
                    .subscribe { elevationBox: ElevationBox ->
                        Log.i(TAG, "test: elevationBox: $elevationBox")
                        Log.i(TAG, "test: Thread.currentThread().name: ${Thread.currentThread().name}")
                        //    Thread.currentThread().name
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

    private fun subscribeMarkerDrag(id: Long, source: Observable<MapPoint>) {
        val pointInfo: PointInfo? = markers.find { it.markerId == id }

        compositeDisposable.add(source
                .observeOn(schedulerProvider.ui())
                .subscribe({ mapPoint ->
                            pointInfo?.let {
                                it.position = mapPoint
                                it.source?.updateFinishGeoPoint(it.position)
                                it.destination?.updateStartGeoPoint(it.position)

                                calculateTotalDistance()

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

                                var distance = 0F

                                for (pointInfoLocal in markers) {
                                    mapModel.updateMarkerTitle(pointInfoLocal.markerId, String.format(Locale.ENGLISH, "%.1f, m\n%.07f °\n%.07f °", distance, pointInfoLocal.position.latitude, pointInfoLocal.position.longitude))

                                    pointInfoLocal.destination?.let {
                                        distance += it.distance
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
        getView()?.updateDistance(String.format(Locale.ENGLISH, "%.1f, m", 0F))
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
        additionalState = lastAdditionalState

        setVisibility(true)
    }

    override fun pause() {
        lastAdditionalState = additionalState

        additionalState = INIT

        setVisibility(false)
    }

    override fun onDetach() {
        clear()

        super.onDetach()
    }

    private fun setVisibility(visibility: Boolean) {
        for (pointInfo in markers) {
            mapModel.setMarkerVisibility(pointInfo.markerId, visibility)
        }

        if (profileLineId != -1L) {
            mapModel.setPolylineVisibility(profileLineId, visibility)
        }
    }

    private fun onMapClick(mapPoint: MapPoint) {
        if (additionalState == ADD_POINT) {
            if (markers.isEmpty()) {
                profileLineId = mapModel.addPolyline(R.color.profileLine, LINE_WIDTH.toFloat())
            }

            val pointInfo = PointInfo(mapPoint)

            pointInfo.markerId = mapModel.addMarker(mapPoint)

            var distance = 0F

            if (markers.isNotEmpty()) {
                val oldLastPointInfo = markers.last()

                val wayInfo = WayInfo(oldLastPointInfo.position, mapPoint)

                oldLastPointInfo.destination = wayInfo
                pointInfo.source = wayInfo

                ways.add(wayInfo)

                distance = calculateTotalDistance()

                updateElevations(wayInfo)
            }

            markers.add(pointInfo)

            mapModel.updateMarkerTitle(pointInfo.markerId, String.format(Locale.ENGLISH, "%.1f, m\n%.07f °\n%.07f °", distance, mapPoint.latitude, mapPoint.longitude))
            mapModel.setMarkerDraggable(pointInfo.markerId, true)

            updateWay()
        }
    }

    private fun clear() {
        mapModel.removeMarkers(markers.map{ it.markerId })

        if (profileLineId != -1L) {
            mapModel.removePolyline(profileLineId)

            profileLineId = -1L
        }

        markers.clear()
        ways.clear()
    }

    private fun calculateTotalDistance(): Float {
        val distance = ways.asSequence().map{ it.distance }.sum()

        getView()?.updateDistance(String.format(Locale.ENGLISH, "%.1f, m", distance))

        return distance
    }

    private fun updateWay() {
        if (profileLineId != -1L) {
            mapModel.updatePolylineMapPoints(profileLineId, markers.map { it.position })
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
