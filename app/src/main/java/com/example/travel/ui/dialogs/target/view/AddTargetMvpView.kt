package com.example.travel.ui.dialogs.target.view

import com.example.travel.ui.base.view.DialogMvpView

interface AddTargetMvpView : DialogMvpView {
    fun setName(name: String)

    fun setLatitude(latitude: String)

    fun setLongitude(longitude: String)
}
