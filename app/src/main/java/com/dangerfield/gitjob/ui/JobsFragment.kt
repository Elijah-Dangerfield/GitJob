package com.dangerfield.gitjob.ui


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.api.Resource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_jobs.*

class JobsFragment: Fragment(R.layout.fragment_jobs) {

    private val jobsAdapter: JobsAdapter by lazy {JobsAdapter(context!!)}

    private val jobsViewModel : JobsViewModel by viewModels()

    private val filterSheet by lazy {setupBottonSheet()}


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_jobs.layoutManager = LinearLayoutManager(context)
        rv_jobs.adapter = jobsAdapter


        jobsViewModel.getJobs().observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> jobsAdapter.jobs = it.data ?: listOf()
                is Resource.Loading -> Log.d("Elijah", "loading")
                is Resource.Error -> Log.d("Elijah", "error: " + it.message)
            }
        })


        btn_filter.setOnClickListener { filterSheet.show() }



    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("Elijah", "on destroy called")
    }


    fun setupBottonSheet(): BottomSheetDialog {
        val view = layoutInflater.inflate(R.layout.fragment_filters_modal, null)
        val filtersModal = BottomSheetDialog(context!!)
        filtersModal.setContentView(view)
        return filtersModal
    }
}
