package com.example.travel.ui.base.presenter

import com.example.travel.ui.base.view.MvpView

interface MvpPresenter<V : MvpView> {
    fun onAttach(mvpView: V)

    fun onDetach()

    fun getView(): V?
}
