package com.example.travel.ui.tool_window_bars.tools.view

import com.example.travel.ui.base.view.MvpView
import com.example.travel.ui.tool_window_bars.tools.presenter.ToolsPresenter

interface ToolsMvpView : MvpView {
    fun setAddButtonColor(color: Int)

    fun updateDistance(value: String)

    fun changeProfileElevation(ways: List<ToolsPresenter.WayInfo>)

    fun clearProfileElevation()

    fun showSelectLevelsDialog()

    fun showCancelDownloadDialog()

    fun showProgressDownload()

    fun updateProgressDownload(progressStr: String, squareNumberCurrent: Int, squareNumberFinal: Int)

    fun changeProgressDownload(messageId: Int)
}
