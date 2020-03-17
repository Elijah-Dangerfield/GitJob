package com.dangerfield.gitjob.ui.jobs.feed

import com.dangerfield.gitjob.model.JobListing

interface ListingSaver {
    fun saveListing(listing: JobListing)
    fun unsaveListing(listing: JobListing)
}