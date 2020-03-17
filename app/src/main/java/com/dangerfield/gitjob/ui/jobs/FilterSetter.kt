package com.dangerfield.gitjob.ui.jobs

interface FilterSetter {
    fun onSetFilter(filter: String? = null)
    fun onSetCity(city: String)
    fun onSetSearchTerm(term: String)
}