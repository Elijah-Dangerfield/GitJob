package com.dangerfield.gitjob.ui.jobs.filter

import com.dangerfield.gitjob.ui.jobs.feed.Filter

interface FilterSetter {
    fun onSetFilter(filter: Filter? = null)
    fun onSetCity(city: String?)
    fun onSetSearchTerm(term: String)
}