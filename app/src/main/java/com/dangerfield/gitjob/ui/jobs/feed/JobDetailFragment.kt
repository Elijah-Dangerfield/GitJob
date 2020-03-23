package com.dangerfield.gitjob.ui.jobs.feed


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.util.console
import com.dangerfield.gitjob.util.goneIf
import kotlinx.android.synthetic.main.fragment_job_detail.*

class JobDetailFragment : Fragment(R.layout.fragment_job_detail) {

    lateinit var mJobListing: JobListing

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_back.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    override fun onResume() {
        super.onResume()

        Glide.with(iv_job.context)
            .load(mJobListing.company_logo)
            .placeholder(R.color.colorPrimary)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv_job)
        tv_company_name.text = mJobListing.company
        tv_title.text = mJobListing.title
        tv_description.text = mJobListing.description
        tv_job_type.text = mJobListing.type
        tv_how_to_apply.text = mJobListing.how_to_apply

        tv_how_to_apply.goneIf(mJobListing.how_to_apply.isNullOrEmpty())
        tv_how_to_apply_title.goneIf(mJobListing.how_to_apply.isNullOrEmpty())

        btn_apply.goneIf(mJobListing.url == null)

        btn_apply.setOnClickListener {
            console.log("clicked on apply to link: ${mJobListing.url}")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mJobListing.url))
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(listing: JobListing): JobDetailFragment {
            val fragment = JobDetailFragment()
            fragment.mJobListing = listing
            return fragment
        }
    }
}
