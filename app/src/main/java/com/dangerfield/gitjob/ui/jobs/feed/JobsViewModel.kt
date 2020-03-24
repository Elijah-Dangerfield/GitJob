package com.dangerfield.gitjob.ui.jobs.feed

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.AddedLocation
import com.dangerfield.gitjob.model.JobListing

enum class Filter(val jobtype: String){
    FULL_TIME("Full Time"),
    PART_TIME("Part Time")
}

class JobsViewModel(private val repository: Repository) : ViewModel(){


    private var jobs = MutableLiveData<Resource<List<JobListing>>>()
    private var selectedLocation = MutableLiveData<String?>()
    private var searchTerm = MutableLiveData<String?>(null)
    var determinedLocation = MutableLiveData<String?>()
    var filter = MutableLiveData<Filter?>(null)


    fun setSelectedLocation(city: String?, refresh: Boolean? = null) {
        selectedLocation.value = city

        if (refresh == true && city != null) refreshListings()
    }

    fun getSelectedLocation() = selectedLocation

    fun setSearchTerm(term: String?) {
        searchTerm.value = term
        refreshListings()
    }

    fun getSearchTerm() = searchTerm

    fun jobsForcePost() {
        jobs.value = jobs.value
    }

    fun getJobs(): MutableLiveData<Resource<List<JobListing>>> {
        if (jobs.value?.data.isNullOrEmpty()) {
             jobs =  repository.getJobs() as MutableLiveData<Resource<List<JobListing>>>
        }
        return jobs
    }

    fun getUserCity(location: Location) = repository.getCity(location)


    fun refreshListings(){
        forceFetch(location = selectedLocation.value, description = searchTerm.value)
    }

    private fun forceFetch(location : String? = null, description: String? = null) {
        repository.forceFetchJobs(location, description)
    }

    fun saveLocation(location: AddedLocation) { repository.saveSearchedLocation(location) }

    fun saveListing(listing: JobListing) {
        repository.saveJob(listing.toSaveable())
    }

    fun unsaveListing(listing: JobListing) {
        repository.unsaveJob(listing.toSaveable())
    }
}