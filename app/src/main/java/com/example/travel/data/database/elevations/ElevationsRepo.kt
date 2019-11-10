package com.example.travel.data.database.elevations

import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ElevationsRepo {
    var elevationBox: ElevationBox?

    fun seedElevationBox(): Observable<ElevationBox>

    fun clear(): Completable

    fun addElevations(squareNumber: Int, elevationsList: List<Int>): Completable

    fun getElevations(linePoints: List<LinearCoordinate<Int, Int>>): Single<List<Int>>

}