package com.example.travel.ui.map.presenter

import com.example.travel.di.PerActivity
import com.example.travel.ui.base.presenter.MvpPresenter
import com.example.travel.ui.map.view.MapMvpView

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint

@PerActivity
interface MapMvpPresenter<V : MapMvpView> : MvpPresenter<V> {
    fun onViewInitialized()

    fun onMapClick(geoPoint: GeoPoint)

    fun onMyLocationButtonClick()

    fun onBoundingBoxChanged(box: BoundingBox)

    fun onMarkerClick(markerId: Long)
}
