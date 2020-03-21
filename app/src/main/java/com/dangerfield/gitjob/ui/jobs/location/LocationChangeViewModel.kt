package com.dangerfield.gitjob.ui.jobs.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.model.AddedLocation

enum class UserState {
    TYPING, SEARCHED, INITAL
}

interface SearchedLocationPersister{
    fun removeSearchedLocation(term: AddedLocation)
    fun saveSearchedLocation(term: AddedLocation)
}

class LocationChangeViewModel(private val repository: Repository): ViewModel(), SearchedLocationPersister {
    override fun removeSearchedLocation(term: AddedLocation) {
        repository.removeSearchedLocation(term)
    }

    override fun saveSearchedLocation(term: AddedLocation) {
        repository.saveSearchedLocation(term)
    }

    fun getSearchedLocations() = repository.getSearchedLocations()


    var userState = MutableLiveData<UserState>(UserState.INITAL)
}