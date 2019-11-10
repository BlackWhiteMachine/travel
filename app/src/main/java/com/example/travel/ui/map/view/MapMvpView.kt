package com.example.travel.ui.map.view

import com.example.travel.ui.base.view.MvpView

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

interface MapMvpView : MvpView {
    fun getBoundingBox(): BoundingBox

    fun addMarker(id: Long, geoPoint: GeoPoint)

    fun showInfoWindowMarker(id: Long)

    fun setMarkerDraggable(id: Long, isDraggable: Boolean)

    fun setMarkerDragListener(id: Long, listener: Marker.OnMarkerDragListener?)

    fun updateMarkerTitle(id: Long, title: String)

    fun removeMarker(id: Long)

    fun addLine(id: Long, color: Int, width: Float)

    fun updateLinePoints(id: Long, mapPointsList: List<GeoPoint>)

    fun removeLine(id: Long)

    fun downloadVisibleTiles(boundingBox: BoundingBox, startZoomLevelValue: Int, finishZoomLevelValue: Int)

    fun invalidate()

    fun setFocusOnMyLocation()
}
