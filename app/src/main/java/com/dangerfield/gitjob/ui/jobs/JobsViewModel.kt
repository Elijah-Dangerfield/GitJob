package com.dangerfield.gitjob.ui.jobs

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.JobListing

class JobsViewModel(val repository: Repository) : ViewModel(),
    ListingSaver {


    private var jobs = MutableLiveData<Resource<List<JobListing>>>()
    private var location = MutableLiveData<String?>()
    private var searchTerm = MutableLiveData<String?>()

    fun setLocation(city: String?, refresh: Boolean? = null) {
        location.value = city
        if (refresh == true && city != null) refreshListings()
    }

    fun getLocation() = location

    fun setSearchTerm(term: String?) {
        searchTerm.value = term
        refreshListings()
    }

    fun getSearchTerm() = searchTerm

    fun getJobs(): MutableLiveData<Resource<List<JobListing>>> {
        Log.d("Elijah", "getting jobs with location: "+ location.value + " and term: " + searchTerm.value)

        if (jobs.value?.data.isNullOrEmpty()) {
            Log.d("Elijah", "fetching new values")
             jobs =  repository.getJobs() as MutableLiveData<Resource<List<JobListing>>>
        }
        return jobs
    }

    fun getUserCity(location: Location) = repository.getCity(location)


    fun refreshListings(){
        Log.d("Elijah", "refreshing with location: "+ location.value + " and term: " + searchTerm.value)
        repository
            .getJobs(location = location.value, description = searchTerm.value, refreshing = true)
                as MutableLiveData<Resource<List<JobListing>>>
    }

    override fun saveListing(listing: JobListing) {
        repository.saveJob(listing.toSaveable())
    }

    override fun unsaveListing(listing: JobListing) {
        repository.unsaveJob(listing.toSaveable())
    }
}