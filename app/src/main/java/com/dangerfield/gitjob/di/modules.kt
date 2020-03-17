package com.dangerfield.gitjob.di

import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.ui.SavedJobsViewModel
import com.dangerfield.gitjob.ui.jobs.JobsViewModel
import com.dangerfield.gitjob.ui.jobs.LocationChangeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // singleton of the  repository

    single { Repository( androidApplication() ) }

    // create the viewModel with the repository dependency INJECTED :)
    viewModel { JobsViewModel(get()) }

    viewModel { LocationChangeViewModel() }

    viewModel { SavedJobsViewModel(get()) }

}