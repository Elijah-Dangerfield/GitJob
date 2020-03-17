package com.dangerfield.gitjob.ui.jobs.filter

interface FilterSetter {
    fun onSetFilter(filter: String? = null)
    fun onSetCity(city: String)
    fun onSetSearchTerm(term: String)
}