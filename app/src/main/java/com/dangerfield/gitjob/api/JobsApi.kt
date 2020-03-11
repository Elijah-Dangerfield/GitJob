package com.dangerfield.gitjob.api

import com.dangerfield.gitjob.model.JobListing
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JobsApi {

    @GET("positions.json")
    fun getListings(
        @Query("location") location: String? = null,
        @Query("description") description: String? = null
        ): Call<List<JobListing>>
}