package com.example.travel.ui.dialogs

import com.example.travel.ui.dialogs.download.DownloadTilesModule
import com.example.travel.ui.dialogs.download.view.DownloadTilesDialog
import com.example.travel.ui.dialogs.target.AddTargetDialogModule
import com.example.travel.ui.dialogs.target.view.AddTargetDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DialogsProvider {

    @ContributesAndroidInjector(modules = [AddTargetDialogModule::class])
    internal abstract fun provideAddTargetDialog() : AddTargetDialog

    @ContributesAndroidInjector(modules = [DownloadTilesModule::class])
    internal abstract fun provideDownloadTilesDialog() : DownloadTilesDialog

}