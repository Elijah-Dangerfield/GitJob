package com.dangerfield.gitjob.ui.jobs.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.JobListing
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.item_job_listing.view.*

class JobsAdapter(private val context: Context, val persistance: ListingSaver, val onItemClicked: ((JobListing) -> Unit)): RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    var jobs = listOf<JobListing>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyLogo: ImageView = view.iv_logo
        val positionTitle: TextView = view.tv_job_title
        val description: TextView = view.tv_job_description
        val saveButton: LikeButton = view.btn_save

        init {

            this.itemView.setOnClickListener {
                onItemClicked(jobs[adapterPosition])
            }
            saveButton.setOnLikeListener(object: OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    persistance.saveListing(jobs[adapterPosition])
                    jobs[adapterPosition].saved = true
                    notifyDataSetChanged()
                }
                override fun unLiked(likeButton: LikeButton?) {
                    persistance.unsaveListing(jobs[adapterPosition])
                    jobs[adapterPosition].saved = false
                    notifyDataSetChanged()
                }
            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_job_listing, parent, false))
    }

    override fun getItemCount() = jobs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]

        holder.saveButton.isLiked = job.saved == true

        holder.apply {
            positionTitle.text = job.title
            description.text = job.description

            Glide.with(companyLogo.context)
                .load(job.company_logo)
                .placeholder(R.color.colorPrimary)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(companyLogo)
        }
    }
}
