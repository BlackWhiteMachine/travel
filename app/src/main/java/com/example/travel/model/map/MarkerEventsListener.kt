package com.example.travel.model.map

interface MarkerEventsListener {
    fun onMarkerClick(id: Long)

    fun onMarkerDragStart(id: Long, mapPoint: MapPoint)

    fun onMarkerDrag(mapPoint: MapPoint)

    fun onMarkerDragEnd(mapPoint: MapPoint)
}