package com.dangerfield.gitjob.ui.saved

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob

class SavedJobsViewModel(private val repository: Repository) : ViewModel() {


     val getSavedJobs = { repository.getSavedJobs() }

     fun deleteSavedJob(savedJob: SavedJob) { repository.unsaveJob(savedJob) }

     fun resaveJobe(job: JobListing) {repository.saveJob(job.toSaveable())}

}