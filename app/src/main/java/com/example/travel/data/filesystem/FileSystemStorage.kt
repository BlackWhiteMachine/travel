package com.example.travel.data.filesystem

import android.content.Context
import android.os.Environment
import android.util.Log

import com.example.travel.di.ApplicationContext

import java.io.File

import javax.inject.Inject

class FileSystemStorage @Inject
constructor(@ApplicationContext context: Context) : IFileSystemStorage {

    companion object {
        private val TAG = FileSystemStorage::class.java.simpleName
    }

    private var cacheDir: File

    init {
        val dirs = context.externalCacheDirs
        for (i in dirs.indices) {
            Log.d(TAG, "dirs: " + dirs[i])
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState())
        }

        cacheDir = if (dirs.size == 2) dirs[1] else dirs[0]
    }

    override fun getCacheDir(): File {
        return cacheDir
    }

    override fun getSubDir(name: String): File {
        val subDir = File(cacheDir.toString() + File.separator + name)
        if (!subDir.isDirectory) {
            subDir.mkdir()
        }

        return subDir
    }
}
