package com.example.travel.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.travel.data.database.AppDatabase
import com.example.travel.data.database.elevations.ElevationsRepo
import com.example.travel.data.database.elevations.ElevationsRepository
import com.example.travel.data.filesystem.FileSystemStorage
import com.example.travel.data.filesystem.IFileSystemStorage
import com.example.travel.data.network.BingElevationDataSource
import com.example.travel.data.preferences.RectangleParametersStorageSharedPreferences
import com.example.travel.di.ApplicationContext
import com.example.travel.di.DatabaseInfo
import com.example.travel.data.network.ElevationDataSource
import com.example.travel.data.preferences.RectangleParametersStorage
import com.example.travel.util.AppConstants
import com.example.travel.util.rx.AppSchedulerProvider
import com.example.travel.util.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @ApplicationContext
    internal fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    internal fun provideIStorage(storage: FileSystemStorage): IFileSystemStorage = storage

    @Provides
    @Singleton
    internal fun provideAppDatabase(@DatabaseInfo dbName: String, @ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                    .fallbackToDestructiveMigration()
                    .build()
    @Provides
    @Singleton
    internal fun provideElevationsRepo(appDatabase: ElevationsRepository): ElevationsRepo = appDatabase

    @Provides
    @Singleton
    internal fun provideRetrofit(): Retrofit =
            Retrofit.Builder()
                .baseUrl(AppConstants.BING_ELEVATION_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

    @Provides
    @DatabaseInfo
    internal fun provideDatabaseName(): String = AppConstants.DB_NAME

    @Provides
    @Singleton
    internal fun provideRectangleParametersStorage(rectangleParametersStorage: RectangleParametersStorageSharedPreferences): RectangleParametersStorage = rectangleParametersStorage

    @Provides
    @Singleton
    internal fun provideElevationDataSource(dataSource: BingElevationDataSource): ElevationDataSource = dataSource

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    internal fun provideSchedulerProvider(schedulerProvider: AppSchedulerProvider): SchedulerProvider = schedulerProvider
}
