package com.dangerfield.gitjob.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JobsClient {
    private const val BASE_URL = "https://jobs.github.com/"

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: JobsApi by lazy {
        retrofitBuilder
            .build()
            .create(JobsApi::class.java)
    }
}