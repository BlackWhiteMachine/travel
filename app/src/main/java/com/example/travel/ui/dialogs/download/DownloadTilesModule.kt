package com.example.travel.ui.dialogs.download

import com.example.travel.ui.dialogs.download.interactor.DownloadTilesInteractor
import com.example.travel.ui.dialogs.download.interactor.DownloadTilesMvpInteractor
import com.example.travel.ui.dialogs.download.presenter.DownloadTilesMvpPresenter
import com.example.travel.ui.dialogs.download.presenter.DownloadTilesPresenter
import com.example.travel.ui.dialogs.download.view.DownloadTilesMvpView
import dagger.Binds
import dagger.Module

@Module
abstract class DownloadTilesModule {

    @Binds
    internal abstract fun provideDownloadTilesInteractor(interactor: DownloadTilesInteractor): DownloadTilesMvpInteractor

    @Binds
    internal abstract fun provideDownloadTilesPresenter(presenter: DownloadTilesPresenter<DownloadTilesMvpView, DownloadTilesMvpInteractor>): DownloadTilesMvpPresenter<DownloadTilesMvpView>

}