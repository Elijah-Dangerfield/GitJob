package com.dangerfield.gitjob.api

import androidx.lifecycle.MutableLiveData
import com.dangerfield.gitjob.model.JobListing

interface JobsRepository {
    fun getJobs(location: String? = null, description : String? = null) : MutableLiveData<Resource<List<JobListing>>>;
    fun getSavedJobs();
}