package com.example.travel.data.network

import com.example.travel.data.network.model.Elevations

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessagesApi {
    @GET("REST/v1/Elevation/{Path}")
    fun elevations(@Path("Path") path: String,
                   @Query("bounds") bounds: String,
                   @Query("rows") rows: String,
                   @Query("cols") cols: String,
                   @Query("heights") heights: String,
                   @Query("key") key: String): Observable<Elevations>
}
