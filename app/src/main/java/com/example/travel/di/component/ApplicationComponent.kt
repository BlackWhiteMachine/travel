package com.example.travel.di.component

import android.app.Application
import com.example.travel.App
import com.example.travel.di.builder.ActivityBuilder
import com.example.travel.di.module.ApplicationModule
import com.example.travel.model.map.MapModel
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, ActivityBuilder::class])
interface ApplicationComponent {

    val mapModel: MapModel

    fun inject(application: App)

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}
