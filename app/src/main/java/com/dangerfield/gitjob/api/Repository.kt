package com.dangerfield.gitjob.api

import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.model.JobListing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Repository: JobsRepository {

    override fun getJobs(
        location: String?,
        description: String?
    ): MutableLiveData<Resource<List<JobListing>>> {
        val result = MutableLiveData<Resource<List<JobListing>>>()

        result.postValue(Resource.Loading())

        RetrofitBuilder.apiService.getListings().enqueue(object:
            Callback<List<JobListing>> {

            override fun onFailure(call: Call<List<JobListing>>, t: Throwable) {
                result.postValue(Resource.Error(message = t.localizedMessage ?: "unknown error"))
            }

            override fun onResponse(
                call: Call<List<JobListing>>,
                response: Response<List<JobListing>>
            ) {
                if (response.isSuccessful) {
                    result.postValue(Resource.Success(data = response.body() ?: listOf()))
                } else {
                    result.postValue(Resource.Success(data =  listOf()))
                }
            }

        })

        return result

    }

    override fun getSavedJobs() {
    }
}