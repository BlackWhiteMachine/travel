package com.example.travel.ui.dialogs.download.interactor

import com.example.travel.ui.base.interactor.MvpInteractor

interface DownloadTilesMvpInteractor : MvpInteractor {
    fun downloadArea(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double)
}