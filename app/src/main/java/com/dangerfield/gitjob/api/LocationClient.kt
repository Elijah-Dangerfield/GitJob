package com.dangerfield.gitjob.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LocationClient {
    private const val BASE_URL = "http://www.mapquestapi.com/"

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: LocationsApi by lazy {
        retrofitBuilder
            .build()
            .create(LocationsApi::class.java)

    }
}