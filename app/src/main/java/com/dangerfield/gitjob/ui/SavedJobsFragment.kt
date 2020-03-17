package com.dangerfield.gitjob.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import kotlinx.android.synthetic.main.fragment_saved_jobs.*
import org.koin.android.viewmodel.ext.android.viewModel

class SavedJobsFragment : Fragment(R.layout.fragment_saved_jobs) {

    private val savedJobsAdapter by lazy { SavedJobsAdapter(context!!) }

    private val savedJobsViewModel : SavedJobsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedJobsViewModel.getSavedJobs().observe(viewLifecycleOwner, Observer {
            savedJobsAdapter.jobs = it
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_saved_jobs.layoutManager = LinearLayoutManager(context)
        rv_saved_jobs.adapter = savedJobsAdapter
    }

}
