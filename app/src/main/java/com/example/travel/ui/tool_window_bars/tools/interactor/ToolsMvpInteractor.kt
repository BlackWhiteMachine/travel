package com.example.travel.ui.tool_window_bars.tools.interactor

import com.example.travel.model.map.MapPoint
import com.example.travel.ui.base.interactor.MvpInteractor
import io.reactivex.Observable
import io.reactivex.Single

interface ToolsMvpInteractor : MvpInteractor {
    fun seedElevationBox(): Observable<ElevationBox>

    fun seedDownloadQueue(): Observable<ElevationBox>

    fun seedDownloadProgress(elevationBox: ElevationBox): Observable<Int>

    fun getElevations(startGeoPoint: MapPoint, finishGeoPoint: MapPoint): Single<List<Int>>

}