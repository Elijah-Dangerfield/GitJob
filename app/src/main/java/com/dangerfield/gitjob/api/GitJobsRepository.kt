package com.dangerfield.gitjob.api

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.model.JobListing

interface GitJobsRepository {
    fun getJobs(location: String? = null, description : String? = null) : MutableLiveData<Resource<List<JobListing>>>;
    fun getSavedJobs()
    fun getCity(location: Location): MutableLiveData<Resource<String>>
}