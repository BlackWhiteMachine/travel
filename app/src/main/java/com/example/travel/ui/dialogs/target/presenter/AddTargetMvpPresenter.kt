package com.example.travel.ui.dialogs.target.presenter

import com.example.travel.ui.base.presenter.MvpPresenter
import com.example.travel.ui.dialogs.target.view.AddTargetMvpView

interface AddTargetMvpPresenter<V : AddTargetMvpView> : MvpPresenter<V> {
    fun onViewInitialized()

    fun setTargetName(name: String)

    fun setTargetPosition(latitude: Double, longitude: Double)
}
