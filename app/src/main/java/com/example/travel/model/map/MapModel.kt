package com.example.travel.model.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapModel @Inject constructor() : MarkerEventsListener {

    private val mMapOverlaysSet: MutableSet<Long> = TreeSet()

    private val mapClickEventsSubject: PublishSubject<MapPoint> = PublishSubject.create()

    val mapClickEvents: Observable<MapPoint>
        get() = mapClickEventsSubject

    private val markerDragListenerSubject: PublishSubject<Pair<Long, Observable<MapPoint>>> = PublishSubject.create()

    val markerDragListener: Observable<Pair<Long, Observable<MapPoint>>>
        get() = markerDragListenerSubject

    private val mapActionsSubject: PublishSubject<MapAction> = PublishSubject.create()

    val mapActions: Observable<MapAction>
        get() = mapActionsSubject

    private val visibleAreaSubject: BehaviorSubject<VisibleArea> = BehaviorSubject.create()

    var visibleArea: VisibleArea?
        get() = visibleAreaSubject.value
        set (value) {
            value?.let {
                visibleAreaSubject.onNext(it)
            }
        }

    private var markerDragEvents: PublishSubject<MapPoint>? = null

    private var mapOverlayId: Long = 0

    fun onMapClick(mapPoint: MapPoint) {
        mapClickEventsSubject.onNext(mapPoint)
    }

    fun downloadVisibleTiles(startZoomLevelValue: Int, finishZoomLevelValue: Int) {
        visibleArea?.let {
            mapActionsSubject.onNext(createDownloadVisibleTiles(it, startZoomLevelValue, finishZoomLevelValue))
        }
    }

    fun cancelAllJobs() {}

    fun addMarker(mapPoint: MapPoint): Long {
        val id = ++mapOverlayId

        mMapOverlaysSet.add(id)

        mapActionsSubject.onNext(createAddMarkerAction(id, mapPoint))

        mapActionsSubject.onNext(createInvalidateAction())

        return id
    }


    fun setMarkerDraggable(id: Long, isDraggable: Boolean) {
        if (mMapOverlaysSet.contains(id)) {
            mapActionsSubject.onNext(createSetMarkerDraggable(id, isDraggable))
        }
    }

    fun setMarkerIcon(id: Long, resId: Int) {}

    fun removeMarker(id: Long) {
        if (mMapOverlaysSet.contains(id)) {
            mMapOverlaysSet.remove(id)

            mapActionsSubject.onNext(createRemoveMarkerAction(id))

            mapActionsSubject.onNext(createInvalidateAction())
        }
    }

    fun removeMarkers(idList: List<Long>) {
        for(id in idList) {
            if (mMapOverlaysSet.contains(id)) {
                mMapOverlaysSet.remove(id)

                mapActionsSubject.onNext(createRemoveMarkerAction(id))
            }
        }

        mapActionsSubject.onNext(createInvalidateAction())
    }

    fun setMarkerVisibility(id: Long, visibility: Boolean) {}

    fun addPolyline(color: Int, width: Float): Long {
        val id = ++mapOverlayId

        mMapOverlaysSet.add(id)

        mapActionsSubject.onNext(createAddLineAction(id, color, width))

        mapActionsSubject.onNext(createInvalidateAction())

        return id
    }

    fun removePolyline(id: Long) {
        if (mMapOverlaysSet.contains(id)) {
            mMapOverlaysSet.remove(id)

            mapActionsSubject.onNext(createRemoveLineAction(id))

            mapActionsSubject.onNext(createInvalidateAction())
        }
    }

    fun updateMarkerTitle(id: Long, title: String) {
        if (mMapOverlaysSet.contains(id)) {
            mapActionsSubject.onNext(createUpdateMarkerTitleAction(id, title))

            mapActionsSubject.onNext(createInvalidateAction())
        }
    }

    fun setPolylineVisibility(id: Long, visibility: Boolean) {}

    fun updatePolylineMapPoints(id: Long, mapPoints: List<MapPoint>) {
        if (mMapOverlaysSet.contains(id)) {
            mapActionsSubject.onNext(createUpdateLineMapPointsAction(id, mapPoints))

            mapActionsSubject.onNext(createInvalidateAction())
        }
    }

    fun showInfoWindow(id: Long) {
        mapActionsSubject.onNext(createShowInfoWindowMarkerAction(id))
    }

    fun hideAllInfoWindows() {}

    override fun onMarkerClick(id: Long) { }

    override fun onMarkerDragStart(id: Long, mapPoint: MapPoint) {
        markerDragEvents = PublishSubject.create()

        addMapMarkerDrag(id, markerDragEvents!!)

        markerDragEvents!!.onNext(mapPoint)
    }

    override fun onMarkerDrag(mapPoint: MapPoint) {
        markerDragEvents?.onNext(mapPoint)
    }

    override fun onMarkerDragEnd(mapPoint: MapPoint) {
        markerDragEvents?.apply {
            onNext(mapPoint)
            onComplete()
        }
    }

    private fun addMapMarkerDrag(id: Long, source: Observable<MapPoint>) {
        markerDragListenerSubject.onNext(Pair(id, source))
    }

}
