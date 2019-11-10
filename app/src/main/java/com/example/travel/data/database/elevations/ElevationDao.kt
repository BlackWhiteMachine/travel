package com.example.travel.data.database.elevations

import androidx.room.Dao
import androidx.room.Query
import com.example.travel.data.database.dao.BaseDao
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ElevationDao : BaseDao<Elevation> {

    @get:Query(" SELECT COUNT(*) FROM elevations")
    val count: Long

    @Query("DELETE FROM elevations")
    fun clearTable(): Completable

    @Query("SELECT * FROM elevations WHERE pointNumber IN (:pointNumbers)")
    fun getByPointNumbers(pointNumbers: List<Long>): Single<List<Elevation>>
}
