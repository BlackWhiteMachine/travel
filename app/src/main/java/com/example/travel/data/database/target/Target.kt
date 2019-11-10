package com.example.travel.data.database.target

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "targets")
class Target {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    var name: String? = null

    var latitude: Double = 0.0

    var longitude: Double = 0.0
}
