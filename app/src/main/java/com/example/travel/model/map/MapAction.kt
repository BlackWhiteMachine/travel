package com.example.travel.model.map

    fun createAddMarkerAction(id: Long, mapPoint: MapPoint): MapAction {
        return AddMarker(id, mapPoint)
    }

    fun createShowInfoWindowMarkerAction(id: Long): MapAction {
        return ShowInfoWindowMarker(id)
    }

    fun createRemoveMarkerAction(id: Long): MapAction {
        return RemoveMarker(id)
    }

    fun createSetMarkerDraggable(id: Long, isDraggable: Boolean): MapAction {
        return SetMarkerDraggable(id, isDraggable)
    }

    fun createUpdateMarkerTitleAction(id: Long, title: String): MapAction {
        return UpdateMarkerTitle(id, title)
    }

    fun createAddLineAction(id: Long, color: Int, width: Float): MapAction {
        return AddLine(id, color, width)
    }

    fun createUpdateLineMapPointsAction(id: Long, mapPointsList: List<MapPoint>): MapAction {
        return UpdateLinePoints(id, mapPointsList)
    }

    fun createRemoveLineAction(id: Long): MapAction {
        return RemoveLine(id)
    }

    fun createDownloadVisibleTiles(visibleArea: VisibleArea, startZoomLevelValue: Int, finishZoomLevelValue: Int): MapAction {
        return DownloadVisibleTiles(visibleArea, startZoomLevelValue, finishZoomLevelValue)
    }

    fun createInvalidateAction(): MapAction {
        return Invalidate()
    }

enum class ActionType {
    ADD_MARKER,
    Show_Info_Window_Marker,
    SET_MARKER_DRAGGABLE,
    REMOVE_MARKER,
    UPDATE_MARKER_TITLE,
    ADD_LINE,
    UPDATE_LINE_POINTS,
    REMOVE_LINE,
    DOWNLOAD_VISIBLE_TILES,
    INVALIDATE
}

sealed class MapAction(var actionType: ActionType)

class AddMarker(val id: Long, val mapPoint: MapPoint)
        : MapAction(ActionType.ADD_MARKER)

class ShowInfoWindowMarker(val id: Long)
    : MapAction(ActionType.Show_Info_Window_Marker)

class SetMarkerDraggable(val id: Long, val isDraggable: Boolean)
        : MapAction(ActionType.SET_MARKER_DRAGGABLE)

class RemoveMarker(val id: Long)
        : MapAction(ActionType.REMOVE_MARKER)

class UpdateMarkerTitle(val id: Long, val title: String)
        : MapAction(ActionType.UPDATE_MARKER_TITLE)

class AddLine(val id: Long, val color: Int, val width: Float)
        : MapAction(ActionType.ADD_LINE)

class UpdateLinePoints(val id: Long, val mapPointsList: List<MapPoint>)
        : MapAction(ActionType.UPDATE_LINE_POINTS)

class RemoveLine(val id: Long) : MapAction(ActionType.REMOVE_LINE)

class DownloadVisibleTiles(val visibleArea: VisibleArea, val startZoomLevelValue: Int, val finishZoomLevelValue: Int)
        : MapAction(ActionType.DOWNLOAD_VISIBLE_TILES)

class Invalidate : MapAction(ActionType.INVALIDATE)


