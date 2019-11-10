package com.example.travel.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import io.reactivex.Completable

interface BaseDao<T> {
    @Insert
    fun insert(value: T): Completable

    @Insert
    fun insert(valueList: List<T>): Completable

    @Update
    fun update(value: T): Completable

    @Delete
    fun delete(value: T): Completable
}
