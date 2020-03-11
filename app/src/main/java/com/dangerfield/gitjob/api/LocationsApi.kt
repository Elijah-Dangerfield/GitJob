package com.dangerfield.gitjob.api

import com.dangerfield.gitjob.model.mapquest.MapQuestResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationsApi {
    @GET("geocoding/v1/reverse")
    fun getMapQuestData(
        @Query("key") key : String,
        @Query("location") location : String,
        @Query("thumbMaps") thumbMaps : Boolean = false
        ): Call<MapQuestResult>
}