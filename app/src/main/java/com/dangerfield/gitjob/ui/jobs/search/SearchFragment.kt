package com.dangerfield.gitjob.ui.jobs.search


import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SearchedTerm
import com.dangerfield.gitjob.ui.jobs.filter.FilterSetter
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.setHideKeyBoardOnPressAway
import com.dangerfield.gitjob.util.onTextChanged
import com.dangerfield.gitjob.util.openKeyboard
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.android.viewmodel.ext.android.viewModel


class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var mFilterSetter: FilterSetter
    private val terms: ArrayList<String> by lazy { ArrayList<String>(resources.getStringArray(R.array.search_auto_complete_list).asList())}
    private val searchAdapter: SearchJobsAdapter by lazy {
        SearchJobsAdapter(
            context!!,
            searchViewModel
        ) { term -> onSelectTerm(term) }
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy {
        AutoCompleteAdapter(
            context!!
        ) { term -> onSelectTerm(term) }
    }

    private val searchViewModel : SearchViewModel by viewModel()


    private fun onSelectTerm(term: String) {
        (etv_search as TextView).text = term
        etv_search.setSelection(etv_search.text.length)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeSearchedTerms()
    }

    private fun observeSearchedTerms() {
        searchViewModel.getSearchedTerms().observe(viewLifecycleOwner, Observer {
            searchAdapter.searches = it
            it.forEach {searched ->
                if(!terms.contains(searched.term.toLowerCase().trim())) { terms.add(searched.term.toLowerCase().trim())
                }
            }
        })
    }

    private fun setupViews() {

        ib_back.setOnClickListener { parentFragmentManager.popBackStackImmediate()}

        etv_search.openKeyboard()
        etv_search.setHideKeyBoardOnPressAway()

        etv_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionID: Int, event: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    search(etv_search.text.toString())
                    return true
                }
                return false
            }

        })

        rv_search.layoutManager = LinearLayoutManager(context)
        rv_search.adapter = searchAdapter

        etv_search.onTextChanged {
            ib_clear_text.goneIf(it.isEmpty())
            rv_search.adapter = if(it.isNotEmpty()) autoCompleteAdapter else searchAdapter
            autoCompleteAdapter.terms = terms.filter {term ->
                term.toLowerCase().contains(etv_search.text.toString().trim().toLowerCase())
            }

        }
        ib_clear_text.setOnClickListener { (etv_search as TextView).text = "" }
    }

    private fun search(term: String?) {
        if(term.isNullOrEmpty()) return
        mFilterSetter.onSetSearchTerm(term)
        searchViewModel.saveSearchTerm(SearchedTerm(term.trim()))
        parentFragmentManager.popBackStackImmediate()
    }

    companion object {
        fun newInstance(filterSetter: FilterSetter): SearchFragment {
            val fragment = SearchFragment()
            fragment.mFilterSetter = filterSetter
            return fragment
        }
    }
}
