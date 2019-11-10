package com.example.travel.ui.dialogs.target.presenter

import com.example.travel.ui.base.presenter.BasePresenter
import com.example.travel.ui.dialogs.target.interactor.AddTargetMvpInteractor
import com.example.travel.ui.dialogs.target.view.AddTargetMvpView
import com.example.travel.util.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class AddTargetPresenter<V : AddTargetMvpView, I : AddTargetMvpInteractor> @Inject
constructor(schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable) : BasePresenter<V, I>(null, schedulerProvider, compositeDisposable), AddTargetMvpPresenter<V> {

    private lateinit var name: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onViewInitialized() {
        getView()?.setName(name)

        getView()?.setLatitude(String.format(Locale.ENGLISH, "%.07f", latitude))
        getView()?.setLongitude(String.format(Locale.ENGLISH, "%.07f", longitude))
    }

    override fun setTargetPosition(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun setTargetName(name: String) {
        this.name = name
    }
}
