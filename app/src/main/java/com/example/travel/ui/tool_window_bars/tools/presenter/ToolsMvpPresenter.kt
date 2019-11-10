package com.example.travel.ui.tool_window_bars.tools.presenter

import com.example.travel.di.PerActivity
import com.example.travel.ui.base.presenter.MvpPresenter
import com.example.travel.ui.tool_window_bars.tools.view.ToolsMvpView

@PerActivity
interface ToolsMvpPresenter<V : ToolsMvpView> : MvpPresenter<V> {
    fun onViewInitialized()

    fun onAddButtonClick()

    fun onClearButtonClick()

    fun buttonDownloadClick()

    fun resume()

    fun pause()

    fun onConfirmCancel()
}
