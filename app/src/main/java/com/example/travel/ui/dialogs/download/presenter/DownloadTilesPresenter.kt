package com.example.travel.ui.dialogs.download.presenter

import android.util.Log
import com.example.travel.model.map.MapModel
import com.example.travel.ui.base.presenter.BasePresenter
import com.example.travel.ui.dialogs.download.interactor.DownloadTilesMvpInteractor
import com.example.travel.ui.dialogs.download.view.DownloadTilesMvpView
import com.example.travel.util.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DownloadTilesPresenter<V : DownloadTilesMvpView, I : DownloadTilesMvpInteractor> @Inject
constructor(interactor: I,
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable,
            private val mapModel: MapModel) :
        BasePresenter<V, I>(interactor, schedulerProvider, compositeDisposable), DownloadTilesMvpPresenter<V> {

    override fun onOkButtonClick(startZoomLevelValue: Int, finishZoomLevelValue: Int) {
        if (startZoomLevelValue > finishZoomLevelValue) {
            getView()?.showMessage("Error! from > to")
        } else {
            val visibleArea = mapModel.visibleArea

            Log.i("DownloadTilesPresenter", "visibleArea: $visibleArea")

            visibleArea?.let {
                mapModel.downloadVisibleTiles(startZoomLevelValue, finishZoomLevelValue)

                interactor?.downloadArea(it.latSouth, it.lonWest, it.latNorth, it.lonEast)
            }

            getView()?.dismissDialog()
        }

    }
    override fun onCancelButtonClick() {
        getView()?.dismissDialog()
    }
}
