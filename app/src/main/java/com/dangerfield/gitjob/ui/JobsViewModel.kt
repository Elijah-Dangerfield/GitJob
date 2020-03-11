package com.dangerfield.gitjob.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.GitJobsRepository
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.JobListing

class JobsViewModel : ViewModel() {

    private val repository: GitJobsRepository = Repository
    var currentCity : String? = null

    override fun onCleared() {
        super.onCleared()

        Log.d("Elijah", "Called on clear in the view model")
    }
    private var jobs = MutableLiveData<Resource<List<JobListing>>>()

    fun getJobs(city: String? = null): MutableLiveData<Resource<List<JobListing>>> {

        currentCity = city
        if (jobs.value?.data.isNullOrEmpty()) {
            Log.d("Elijah", "fetching new values")
             jobs =  repository.getJobs(location = currentCity)
        }
        return jobs
    }


    fun refreshListings(){
        jobs =  repository.getJobs(location = currentCity)
    }
}