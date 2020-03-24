package com.dangerfield.gitjob.api

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.db.GitJobDatabase
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.model.AddedLocation
import com.dangerfield.gitjob.model.SearchedTerm
import com.dangerfield.gitjob.model.mapquest.MapQuestResult
import com.dangerfield.gitjob.util.console
import com.dangerfield.gitjob.util.pmap
import com.dangerfield.gitjob.util.removeHtml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(application: Application): GitJobsRepository {

    private val apiKey = "qoPCzFujaMkzRvdkprCGAfpaVwJy2Phn"
    private val db = GitJobDatabase(application)


    abstract class GithubApiParams {
        abstract var location : String?
        abstract var description: String?
    }

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

        return jobsResource.build(params).asLiveData()
    }

    fun forceFetchJobs( location: String?, description: String?) {
        val params = object : GithubApiParams(){
            override var location: String? = location
            override var description: String? = description
        }
        jobsResource.refresh(params).asLiveData()
    }


    private fun writeToDataBase(listings: List<JobListing>?) {
            CoroutineScope(IO ).launch {
                db.mainDao().updateAll(listings ?: listOf())
            }
    }

    private fun getFromDb(): LiveData<List<JobListing>> = db.mainDao().getAll()

    fun getListingsFromApi(location: String? = null,
                           description: String? = null): LiveData<ApiResponse<List<JobListing>>> {


        console.log("actually calling api in repository with $location and $description")

        val result = MutableLiveData<ApiResponse<List<JobListing>>>()

        if(!Connectivity.isOnline) {
            result.postValue(ApiResponse.Error(null, "Please check your internet connection and try again"))
            return result
        }

        JobsClient.apiService.getListings(location = location, description = description)
            .enqueue(object: Callback<List<JobListing>> {

            override fun onFailure(call: Call<List<JobListing>>, t: Throwable) {
                console.log("got failure in api call of repository")

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
                            var urlCount = 0
                            value.forEach {
                                if(it.saved == true) count++
                                if(!it.url.isNullOrEmpty())  urlCount++
                            }
                            Log.d("Elijah", "Posting api call success with $count items marked as saved")
                            Log.d("Elijah", "Posting api call success with $urlCount urls marked as not null")

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
         return response.pmap {
             it.saved = queryForSavedJob(it).isNotEmpty()
             it.description = it.description?.removeHtml()
             it.title = it.title?.removeHtml()
             if(it.how_to_apply?.contains("http") == true) {
                 it.url = "http" + it.how_to_apply?.substringAfter("\"http")?.substringBefore("\">")
             }
             it.how_to_apply = it.how_to_apply?.removeHtml()
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
            unsaveJobListing(jobListing.id)
        }
    }

    fun unsaveJobListing(id: String) {
        db.mainDao().unsaveJobListing(id)
    }

    fun getSearchedTerms() = db.mainDao().getAllSearchedTerms()

    fun removeSearchTerm(search: SearchedTerm) {
        CoroutineScope(IO).launch {
            db.mainDao().deleteSearchedTerm(search.term.trim())
        }
    }

    fun saveSearchTerm(term: SearchedTerm) {
        CoroutineScope(IO).launch {
            db.mainDao().insertSearchedTerm(term)
        }
    }

    fun getSearchedLocations() = db.mainDao().getAllSearchedLocations()

    fun removeSearchedLocation(location: AddedLocation) {
        CoroutineScope(IO).launch {
            db.mainDao().deleteSearchedLocation(location.location.trim())
        }
    }

    fun saveSearchedLocation(location: AddedLocation) {
        CoroutineScope(IO).launch {
            db.mainDao().insertSearchedLocation(location)
        }
    }
}