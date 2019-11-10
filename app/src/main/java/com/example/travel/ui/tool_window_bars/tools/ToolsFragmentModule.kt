package com.example.travel.ui.tool_window_bars.tools

import com.example.travel.ui.tool_window_bars.tools.interactor.ToolsInteractor
import com.example.travel.ui.tool_window_bars.tools.interactor.ToolsMvpInteractor
import com.example.travel.ui.tool_window_bars.tools.presenter.ToolsMvpPresenter
import com.example.travel.ui.tool_window_bars.tools.presenter.ToolsPresenter
import com.example.travel.ui.tool_window_bars.tools.view.ToolsMvpView
import dagger.Binds
import dagger.Module

@Module
abstract class ToolsFragmentModule {

    @Binds
    internal abstract fun provideToolsInteractor(interactor: ToolsInteractor): ToolsMvpInteractor

    @Binds
    internal abstract fun provideToolsPresenter(presenter: ToolsPresenter<ToolsMvpView, ToolsMvpInteractor>): ToolsMvpPresenter<ToolsMvpView>
}