package com.example.travel.ui.dialogs.download.presenter

import com.example.travel.ui.base.presenter.MvpPresenter
import com.example.travel.ui.dialogs.download.view.DownloadTilesMvpView

interface DownloadTilesMvpPresenter<V : DownloadTilesMvpView> : MvpPresenter<V> {
    fun onOkButtonClick(startZoomLevelValue: Int, finishZoomLevelValue: Int)

    fun onCancelButtonClick()
}
