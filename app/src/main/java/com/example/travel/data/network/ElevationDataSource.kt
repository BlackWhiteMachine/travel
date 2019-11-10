package com.example.travel.data.network

import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import io.reactivex.Observable
import io.reactivex.Single

interface ElevationDataSource {
    fun addDownloadArea(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double)

    fun getDownloadQueue(): Observable<ElevationBox>

    fun performRequest(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double): Observable<List<Int>>
}
