package com.example.travel.ui.map

import com.example.travel.ui.map.interactor.MapInteractor
import com.example.travel.ui.map.interactor.MapMvpInteractor
import com.example.travel.ui.map.presenter.MapMvpPresenter
import com.example.travel.ui.map.presenter.MapPresenter
import com.example.travel.ui.map.view.MapMvpView
import dagger.Binds
import dagger.Module

@Module
abstract class MapFragmentModule {

    @Binds
    internal abstract fun provideMapInteractor(interactor: MapInteractor): MapMvpInteractor

    @Binds
    internal abstract fun provideMapPresenter(presenter: MapPresenter<MapMvpView, MapMvpInteractor>): MapMvpPresenter<MapMvpView>

}