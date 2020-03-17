package com.dangerfield.gitjob.ui.jobs.feed

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.JobListing

class JobsViewModel(private val repository: Repository) : ViewModel(),
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
        if (jobs.value?.data.isNullOrEmpty()) {
             jobs =  repository.getJobs() as MutableLiveData<Resource<List<JobListing>>>
        }
        return jobs
    }

    fun getUserCity(location: Location) = repository.getCity(location)


    fun refreshListings(){
        forceFetch(location = location.value, description = searchTerm.value)
    }

    private fun forceFetch(location : String? = null, description: String? = null) {
        repository.forceFetchJobs(location, description)
    }

    override fun saveListing(listing: JobListing) {
        repository.saveJob(listing.toSaveable())
    }

    override fun unsaveListing(listing: JobListing) {
        repository.unsaveJob(listing.toSaveable())
    }
}