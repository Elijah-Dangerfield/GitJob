package com.dangerfield.gitjob.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.JobsRepository
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.JobListing

class JobsViewModel : ViewModel() {

    private val repository: JobsRepository = Repository

    override fun onCleared() {
        super.onCleared()

        Log.d("Elijah", "Called on clear in the view model")
    }
    private var jobs = MutableLiveData<Resource<List<JobListing>>>()

    fun getJobs(): MutableLiveData<Resource<List<JobListing>>> {

        if (jobs.value?.data.isNullOrEmpty()) {
            Log.d("Elijah", "fetching new values")
             jobs =  repository.getJobs()
        }
        return jobs
    }
}