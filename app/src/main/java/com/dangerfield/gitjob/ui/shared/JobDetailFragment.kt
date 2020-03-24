package com.dangerfield.gitjob.ui.shared


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.ui.jobs.feed.ListingSaver
import com.dangerfield.gitjob.ui.saved.OptionsHandler
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.isShowing
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.fragment_job_detail.*

class JobDetailFragment : Fragment(R.layout.fragment_job_detail) {

    lateinit var mJobListing: JobListing
    lateinit var mPersister: ListingSaver
    lateinit var optionsHandler: OptionsHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_back.setOnClickListener { parentFragmentManager.popBackStackImmediate() }

        scrollview.setOnScrollChangeListener { _, _, _, _, _ ->
            header.isSelected = scrollview.canScrollVertically(-1)
            tv_company_name_header.goneIf(scrollview.isShowing(tv_company_name))
        }

        btn_save.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) { mPersister.saveListing(mJobListing) }

            override fun unLiked(likeButton: LikeButton?) { mPersister.unsaveListing(mJobListing) }
        })
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
        tv_description.text =
            if(mJobListing.description.isNullOrEmpty()) "No descrition provided from ${mJobListing.company}"
            else mJobListing.description

        tv_job_type.text = mJobListing.type
        tv_how_to_apply.text = mJobListing.how_to_apply
        tv_company_name_header.text = mJobListing.company

        tv_how_to_apply.goneIf(mJobListing.how_to_apply.isNullOrEmpty())
        tv_how_to_apply_title.goneIf(mJobListing.how_to_apply.isNullOrEmpty())

        btn_apply.goneIf(mJobListing.url == null)

        btn_apply.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mJobListing.url))
            startActivity(intent)
        }

        btn_save.isLiked = mJobListing.saved == true

        ib_more.setOnClickListener {
            optionsHandler.presentOptions(mJobListing.toSaveable())
        }
    }

    companion object {
        fun newInstance(listing: JobListing,
                        persistence: ListingSaver,
                        optionsHandler: OptionsHandler): JobDetailFragment {

            val fragment = JobDetailFragment()
            fragment.mJobListing = listing
            fragment.mPersister = persistence
            fragment.optionsHandler = optionsHandler
            return fragment

        }
    }
}
