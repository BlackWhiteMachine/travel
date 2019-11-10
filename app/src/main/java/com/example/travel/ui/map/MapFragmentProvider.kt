package com.example.travel.ui.map

import com.example.travel.ui.map.view.MapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MapFragmentProvider {

    @ContributesAndroidInjector(modules = [MapFragmentModule::class])
    internal abstract fun provideMapFragment() : MapFragment

}