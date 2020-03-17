package com.dangerfield.gitjob.api

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.db.GitJobDatabase
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.model.mapquest.LatLng
import com.dangerfield.gitjob.model.mapquest.MapQuestResult
import com.dangerfield.gitjob.util.pmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

class Repository(application: Application): GitJobsRepository {

    private val apiKey = "qoPCzFujaMkzRvdkprCGAfpaVwJy2Phn"
    private val db = GitJobDatabase(application)


    abstract class GithubApiParams {
        abstract var location : String?
        abstract var description: String?
    }
    /*
    for now we will have one network bound resource, i would think we wouldnt wanna cache like searches and stuff.
    so will will end up taking out description most likely and making searching a seperate thing
    that isnt a network bound resource. But for now we will cache the most recent MAIN results with this
    we will also end up caching the location with these so that we can show the data correctly while loading
    and we will need top find a way to pass in the cached locaiton into the first call only.

        TODO: i think a source isnt getting removed or something is happening with this object creation

     */

     private val jobsResource = object :  NetworkBoundResource<List<JobListing>, GithubApiParams>() {
         override fun saveCallResult(item: List<JobListing>) {
             writeToDataBase(item)
         }

         override fun shouldFetch(data: List<JobListing>?): Boolean {
             return true
         }

         override fun loadFromDb(): LiveData<List<JobListing>> {
             return getFromDb()
         }

         override fun createCall(params: GithubApiParams): LiveData<ApiResponse<List<JobListing>>> {
             Log.d("Elijah", "Actually calling api with location: " + params.location + " and desc: " + params.description)
             return getListingsFromApi(params.location, params.description)
         }
     }



    override fun getJobs(
        location: String?,
        description: String?,
        refreshing: Boolean?
    ): LiveData<Resource<List<JobListing>>> {

        val params = object : GithubApiParams(){
            override var location: String? = location
            override var description: String? = description
        }

        return if (refreshing == true) jobsResource.refresh(params).asLiveData() else jobsResource.build(params).asLiveData()
    }


    private fun writeToDataBase(listings: List<JobListing>?) {
            CoroutineScope(IO ).launch {
                db.mainDao().updateAll(listings ?: listOf())
            }
    }

    private fun getFromDb(): LiveData<List<JobListing>> {
        return db.mainDao().getAll()
    }

    fun getListingsFromApi(location: String? = null,
                           description: String? = null): LiveData<ApiResponse<List<JobListing>>> {

        val result = MutableLiveData<ApiResponse<List<JobListing>>>()

        if(!Connectivity.isOnline) {
            result.postValue(ApiResponse.Error(null, ""))
            return result
        }

        JobsClient.apiService.getListings(location = location, description = description)
            .enqueue(object: Callback<List<JobListing>> {

            override fun onFailure(call: Call<List<JobListing>>, t: Throwable) {
                Log.d("Elijah", "Retrofit failure")

                result.postValue(ApiResponse.Error(message = t.localizedMessage ?: "Error getting job listings, please try refreshing"))
            }

            override fun onResponse(
                call: Call<List<JobListing>>,
                response: Response<List<JobListing>>
            ) {
                if (response.isSuccessful) {
                    if((response.body()?.size ?: 0) > 0) {
                        CoroutineScope(IO).launch {
                            val value = processListingResponse(response.body()!!)
                            var count = 0
                            value.forEach { if(it.saved == true) count++ }
                            Log.d("Elijah", "Posting api call success with $count items marked as saved")
                            result.postValue(ApiResponse.Success(value))
                        }
                    }else {
                        val value : ApiResponse<List<JobListing>> =
                            if(description == null) ApiResponse.Empty(
                            "There were no listings found for \"$location\". Showing all saved listings", GitHubErrorMessage.BAD_LOCATION)
                            else ApiResponse.Empty(
                            "There were no listings found for \"$description\" in \"$location\". Showing all saved listings", GitHubErrorMessage.BAD_SEARCH)
                        result.postValue(value)
                    }
                } else {
                    Log.d("Elijah", "Retrofit unsuccesful")

                    result.postValue(ApiResponse.Error(message = response.message()))
                }
            }

        })

        return result
    }

    suspend fun processListingResponse(response: List<JobListing> ): List<JobListing> {
        var count = 0
         return response.pmap {
             it.saved = queryForSavedJob(it).isNotEmpty()
             Log.d("Elijah", "processed the ${++count} query with saved = ${it.saved} ")
             it
         }
    }

    override fun getCity(location: Location): MutableLiveData<Resource<String>> {
        val result = MutableLiveData<Resource<String>>()

        val locationQuery = "${location.latitude},${location.longitude}"
        LocationClient.apiService.getMapQuestData(apiKey,locationQuery).enqueue(object:
            Callback<MapQuestResult> {
            override fun onFailure(call: Call<MapQuestResult>, t: Throwable) {
                result.postValue(Resource.Error(message = t.localizedMessage  ?: "unknown error"))
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

    suspend fun queryForSavedJob(job: JobListing) : List<SavedJob> {
        return db.mainDao().querySavedJob(job.id)
    }

    override fun getSavedJobs(): LiveData<List<SavedJob>> {
        return db.mainDao().getAllSavedJobs()
    }

    fun saveJob(jobListing: SavedJob) {
        CoroutineScope(IO).launch {
            db.mainDao().insertSavedJob(jobListing)
        }
    }

    fun unsaveJob(jobListing: SavedJob) {
        CoroutineScope(IO).launch {

            db.mainDao().deleteSavedJob(jobListing.id)
        }
    }
}