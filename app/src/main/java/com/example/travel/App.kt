package com.example.travel

import android.app.Application
import android.preference.PreferenceManager

import com.example.travel.data.filesystem.FileSystemStorage
import com.example.travel.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector

import org.osmdroid.config.Configuration
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    companion object {
        private const val TILES_DIR = "tiles"
    }

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = activityDispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        val storage = FileSystemStorage(this)

        Configuration.getInstance().osmdroidTileCache = storage.getSubDir(TILES_DIR)
        Configuration.getInstance().osmdroidBasePath = storage.getCacheDir()
    }
}
