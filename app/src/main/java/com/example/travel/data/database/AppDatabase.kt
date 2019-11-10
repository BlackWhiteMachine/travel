package com.example.travel.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travel.data.database.elevations.Elevation
import com.example.travel.data.database.elevations.ElevationDao
import com.example.travel.data.database.target.Target
import com.example.travel.data.database.target.TargetDao

@Database(entities = [Elevation::class, Target::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun elevationDao(): ElevationDao

    abstract fun targetDao(): TargetDao
}
