package com.dangerfield.gitjob.api

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob

interface GitJobsRepository {
    fun getJobs(location: String? = null, description: String? = null,  refreshing: Boolean? = null) : LiveData<Resource<List<JobListing>>>;
    fun getSavedJobs(): LiveData<List<SavedJob>>
    fun getCity(location: Location): MutableLiveData<Resource<String>>
}