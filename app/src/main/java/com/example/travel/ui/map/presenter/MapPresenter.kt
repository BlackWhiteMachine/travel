package com.example.travel.ui.map.presenter

import com.example.travel.model.map.*
import com.example.travel.ui.base.presenter.BasePresenter
import com.example.travel.ui.map.interactor.MapMvpInteractor
import com.example.travel.ui.map.view.MapMvpView
import com.example.travel.util.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.util.*
import javax.inject.Inject

class MapPresenter<V : MapMvpView, I : MapMvpInteractor> @Inject
    internal constructor(interactor: I,
                         schedulerProvider: SchedulerProvider,
                         compositeDisposable: CompositeDisposable,
                         private val mapModel: MapModel) :
            BasePresenter<V, I>(interactor, schedulerProvider, compositeDisposable), MapMvpPresenter<V>, Marker.OnMarkerDragListener {

    companion object {
        private val TAG = MapPresenter::class.java.simpleName
    }

    private var mMapDragEvents: PublishSubject<MapPoint>? = null

    private var mapPoint: MapPoint? = null

    override fun onViewInitialized() {
        getView()?.let {
            onBoundingBoxChanged(it.getBoundingBox())
        }

        compositeDisposable.add(mapModel.mapActions
                .observeOn(schedulerProvider.ui())
                .subscribe { action ->
                    handleAction(action)
                }
        )
    }

    override fun onMapClick(geoPoint: GeoPoint) {
        mapModel.onMapClick(MapPoint(geoPoint.latitude, geoPoint.longitude))
    }

    override fun onMyLocationButtonClick() {
        getView()?.setFocusOnMyLocation()
    }

    override fun onBoundingBoxChanged(box: BoundingBox) {
        val visibleArea = VisibleArea(
                latNorth = box.latNorth,
                latSouth = box.latSouth,
                lonEast = box.lonEast,
                lonWest = box.lonWest)

        mapModel.visibleArea = visibleArea
    }

    override fun onMarkerClick(markerId: Long) {}

    override fun onMarkerDrag(marker: Marker) {
        mapPoint?.let {
            it.latitude = marker.position.latitude
            it.longitude = marker.position.longitude

            mMapDragEvents?.onNext(it)
        }
    }

    override fun onMarkerDragEnd(marker: Marker) {
        mapPoint?.let {
            it.latitude = marker.position.latitude
            it.longitude = marker.position.longitude

            mMapDragEvents?.apply {
                onNext(it)
                onComplete()
            }
        }

        mMapDragEvents = null
        mapPoint = null
    }

    override fun onMarkerDragStart(marker: Marker) {
        val markerId = java.lang.Long.parseLong(marker.id)

        mMapDragEvents = PublishSubject.create()

        mapModel.addMapMarkerDrag(markerId, mMapDragEvents!!)

        mapPoint = MapPoint(marker.position.latitude,
                marker.position.longitude)

        mMapDragEvents!!.onNext(mapPoint!!)
    }

    private fun handleAction(action: MapAction) {
        when (action.actionType) {
            ActionType.ADD_MARKER -> {
                val addMarkerAction = action as AddMarker

                val mapPoint = addMarkerAction.mapPoint

                getView()?.addMarker(addMarkerAction.id, GeoPoint(mapPoint.latitude, mapPoint.longitude))

                getView()?.setMarkerDragListener(addMarkerAction.id, this)
            }

            ActionType.Show_Info_Window_Marker -> {
                val showInfoWindowMarker = action as ShowInfoWindowMarker

                getView()?.showInfoWindowMarker(showInfoWindowMarker.id)
            }

            ActionType.SET_MARKER_DRAGGABLE -> {
                val setMarkerDraggable = action as SetMarkerDraggable

                getView()?.setMarkerDraggable(setMarkerDraggable.id, setMarkerDraggable.isDraggable)
            }

            ActionType.REMOVE_MARKER -> {
                val removeMarkerAction = action as RemoveMarker

                getView()?.setMarkerDragListener(removeMarkerAction.id, null)

                getView()?.removeMarker(removeMarkerAction.id)
            }

            ActionType.UPDATE_MARKER_TITLE -> {
                val updateMarkerTitle = action as UpdateMarkerTitle

                getView()?.updateMarkerTitle(updateMarkerTitle.id, updateMarkerTitle.title)
            }

            ActionType.ADD_LINE -> {
                val addLine = action as AddLine

                getView()?.addLine(addLine.id, addLine.color, addLine.width)
            }

            ActionType.UPDATE_LINE_POINTS -> {
                val updateLinePoints = action as UpdateLinePoints

                val geoPoints = LinkedList<GeoPoint>()

                for (mapPoint in updateLinePoints.mapPointsList) {
                    geoPoints.add(GeoPoint(mapPoint.latitude, mapPoint.longitude))
                }

                getView()?.updateLinePoints(updateLinePoints.id, geoPoints)
            }

            ActionType.REMOVE_LINE -> {
                val removeLine = action as RemoveLine

                getView()?.removeLine(removeLine.id)
            }

            ActionType.DOWNLOAD_VISIBLE_TILES -> {
                val downloadVisibleTiles = action as DownloadVisibleTiles

                val visibleArea = downloadVisibleTiles.visibleArea

                val boundingBox = BoundingBox(
                        visibleArea.latNorth,
                        visibleArea.lonEast,
                        visibleArea.latSouth,
                        visibleArea.lonWest)

                getView()?.downloadVisibleTiles(boundingBox,
                        downloadVisibleTiles.startZoomLevelValue,
                        downloadVisibleTiles.finishZoomLevelValue
                )
            }

            ActionType.INVALIDATE -> {
                getView()?.invalidate()
            }
        }
    }
}
