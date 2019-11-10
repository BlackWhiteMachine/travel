package com.example.travel.ui.dialogs.target

import com.example.travel.ui.dialogs.target.interactor.AddTargetInteractor
import com.example.travel.ui.dialogs.target.interactor.AddTargetMvpInteractor
import com.example.travel.ui.dialogs.target.presenter.AddTargetMvpPresenter
import com.example.travel.ui.dialogs.target.presenter.AddTargetPresenter
import com.example.travel.ui.dialogs.target.view.AddTargetMvpView
import dagger.Binds
import dagger.Module

@Module
abstract class AddTargetDialogModule {

    @Binds
    internal abstract fun provideToolsInteractor(interactor: AddTargetInteractor): AddTargetMvpInteractor

    @Binds
    internal abstract fun provideAddTargetPresenter(presenter: AddTargetPresenter<AddTargetMvpView, AddTargetMvpInteractor>): AddTargetMvpPresenter<AddTargetMvpView>

}