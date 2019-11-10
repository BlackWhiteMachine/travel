package com.example.travel.ui.tool_window_bars.tools

import com.example.travel.ui.tool_window_bars.tools.view.ToolsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ToolsFragmentProvider {

    @ContributesAndroidInjector(modules = [ToolsFragmentModule::class])
    internal abstract fun provideToolsFragment() : ToolsFragment

}