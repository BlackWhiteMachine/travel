package com.example.travel.data.filesystem

import java.io.File

interface IFileSystemStorage {

    fun getCacheDir(): File

    fun getSubDir(name: String): File
}
