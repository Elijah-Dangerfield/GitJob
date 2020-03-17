package com.dangerfield.gitjob.ui.jobs.search

import androidx.lifecycle.ViewModel
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.model.SearchedTerm

interface SearchTermPersister {
    fun removeSearchTerm(term: SearchedTerm)
    fun saveSearchTerm(term: SearchedTerm)
}

class SearchViewModel(private val repository: Repository) : ViewModel(),
    SearchTermPersister {

    override fun removeSearchTerm(term: SearchedTerm) { repository.removeSearchTerm(term) }

    override fun saveSearchTerm(term: SearchedTerm) { repository.saveSearchTerm(term) }

    fun getSearchedTerms() = repository.getSearchedTerms()

}