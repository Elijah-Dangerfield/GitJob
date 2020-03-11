package com.dangerfield.gitjob.api

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.mapquest.LatLng
import com.dangerfield.gitjob.model.mapquest.MapQuestResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Repository: GitJobsRepository {

    private val apiKey = "qoPCzFujaMkzRvdkprCGAfpaVwJy2Phn"

    override fun getJobs(
        location: String?,
        description: String?
    ): MutableLiveData<Resource<List<JobListing>>> {
        val result = MutableLiveData<Resource<List<JobListing>>>()

        result.postValue(Resource.Loading())

        JobsClient.apiService.getListings().enqueue(object:
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

    override fun getCity(location: Location): MutableLiveData<Resource<String>> {
        val result = MutableLiveData<Resource<String>>()

        val locationQuery = "${location.latitude},${location.longitude}"
        LocationClient.apiService.getMapQuestData(apiKey,locationQuery).enqueue(object:
            Callback<MapQuestResult> {
            override fun onFailure(call: Call<MapQuestResult>, t: Throwable) {
                result.postValue(Resource.Error(message = t.localizedMessage ?: "unknown error"))
            }

            override fun onResponse(
                call: Call<MapQuestResult>,
                response: Response<MapQuestResult>
            ) {
                if(response.isSuccessful){
                    val city = response.body()!!.results[0].locations[0].adminArea5
                    result.postValue(Resource.Success(data = city))
                }else{
                    result.postValue(Resource.Error(message = "Unknown City"))
                }
            }

        })
        return result
    }

    override fun getSavedJobs() {
    }
}