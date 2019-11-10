package com.example.travel.di.builder

import com.example.travel.ui.dialogs.DialogsProvider
import com.example.travel.ui.main.MainActivityModule
import com.example.travel.ui.main.view.MainActivity
import com.example.travel.ui.map.MapFragmentProvider
import com.example.travel.ui.tool_window_bars.tools.ToolsFragmentProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(MainActivityModule::class),(ToolsFragmentProvider::class),(MapFragmentProvider::class),(DialogsProvider::class)])
    abstract fun bindMainActivity(): MainActivity

}