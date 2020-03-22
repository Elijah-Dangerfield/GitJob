package com.dangerfield.gitjob.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SavedJob
import kotlinx.android.synthetic.main.fragment_saved_jobs.*
import org.koin.android.viewmodel.ext.android.viewModel

class SavedJobsFragment : Fragment(R.layout.fragment_saved_jobs),
    OptionsPresenter {

    private val savedOptionsModal by lazy { {
        SavedOptionsModal.newInstance(this)
    }()}

    private fun showOptions(savedJob: SavedJob) {
        savedOptionsModal.show(parentFragmentManager, savedJob)
    }

    private val savedJobsAdapter by lazy {
        SavedJobsAdapter(
            context!!,
            this
        )
    }

    private val savedJobsViewModel : SavedJobsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedJobsViewModel.getSavedJobs().observe(viewLifecycleOwner, Observer {
            savedJobsAdapter.jobs = it
        })
    }

    override fun presentOptions(savedJob: SavedJob) { showOptions(savedJob) }

    override fun onShare(savedJob: SavedJob) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Check out this great job I found on Git Job")
            putExtra(Intent.EXTRA_TEXT, savedJob.url!!)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onDelete(savedJob: SavedJob) { savedJobsViewModel.deleteSavedJob(savedJob) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_saved_jobs.layoutManager = LinearLayoutManager(context)
        rv_saved_jobs.adapter = savedJobsAdapter
    }

}
