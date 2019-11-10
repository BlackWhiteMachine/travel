package com.example.travel.data.database.target

import androidx.room.Dao
import androidx.room.Query
import com.example.travel.data.database.dao.BaseDao
import io.reactivex.Single

@Dao
interface TargetDao : BaseDao<Target> {

    @Query("SELECT * FROM targets WHERE id IN (:idList)")
    fun getById(idList: List<Long>): List<Target>

    @Query("SELECT * FROM targets WHERE latitude  BETWEEN :latitudeSouth AND :latitudeNorth AND longitude  BETWEEN :longitudeWest AND :longitudeEast")
    fun getInArea(latitudeSouth: Double, latitudeNorth: Double, longitudeWest: Double, longitudeEast: Double): Single<List<Target>>
}
