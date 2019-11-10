package com.example.travel.ui.base.view

import androidx.annotation.StringRes

interface MvpView {
    fun onError(@StringRes resId: Int)

    fun onError(message: String?)

    fun showMessage(message: String?)

    fun showMessage(@StringRes resId: Int)

    fun hideKeyboard()
}
