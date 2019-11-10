package com.example.travel.ui.base.presenter

import com.example.travel.ui.base.interactor.MvpInteractor
import com.example.travel.ui.base.view.MvpView
import com.example.travel.util.rx.SchedulerProvider

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<V : MvpView, I : MvpInteractor> internal constructor(protected var interactor: I?, protected val schedulerProvider: SchedulerProvider, protected val compositeDisposable: CompositeDisposable) : MvpPresenter<V> {

    private var mvpView: V? = null
    private val isViewAttached: Boolean get() = mvpView != null

    override fun onAttach(mvpView: V) {
        this.mvpView = mvpView
    }

    override fun getView(): V? = mvpView

    override fun onDetach() {
        compositeDisposable.dispose()

        mvpView = null
    }
}
