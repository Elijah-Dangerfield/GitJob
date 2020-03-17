package com.dangerfield.gitjob.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.model.SavedJob

class SavedJobsViewModel(val repository: Repository) : ViewModel() {


     val getSavedJobs = { repository.getSavedJobs() }

}