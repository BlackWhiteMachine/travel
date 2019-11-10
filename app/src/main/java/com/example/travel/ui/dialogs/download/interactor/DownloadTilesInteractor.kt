package com.example.travel.ui.dialogs.download.interactor

import com.example.travel.data.network.ElevationDataSource
import com.example.travel.ui.base.interactor.BaseInteractor
import javax.inject.Inject

class DownloadTilesInteractor @Inject constructor(private val elevationDataSource: ElevationDataSource) : BaseInteractor(), DownloadTilesMvpInteractor {
    override fun downloadArea(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double) {
        elevationDataSource.addDownloadArea(latSouth, lonWest, latNorth, lonEast)
    }
}