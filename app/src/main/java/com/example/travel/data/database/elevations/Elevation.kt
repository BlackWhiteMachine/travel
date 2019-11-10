package com.example.travel.data.database.elevations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "elevations")
class Elevation {

    @PrimaryKey
    var pointNumber: Long = 0

    var elevation: Int = 0

}
