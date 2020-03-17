package com.dangerfield.gitjob.ui


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SavedJob
import kotlinx.android.synthetic.main.item_saved_job_listing.view.*
interface OptionsPresenter{
    fun presentOptions(savedJob: SavedJob)
    fun onShare(savedJob: SavedJob)
    fun onDelete(savedJob: SavedJob)
}

class SavedJobsAdapter(private val context: Context, private val optionsPresenter: OptionsPresenter): RecyclerView.Adapter<SavedJobsAdapter.ViewHolder>() {

    var jobs = listOf<SavedJob>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyLogo: ImageView = view.iv_logo
        val positionTitle: TextView = view.tv_job_title
        val description: TextView = view.tv_job_description
        val options: ImageButton = view.ib_details

        init {
            options.setOnClickListener {
                optionsPresenter.presentOptions(jobs[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved_job_listing, parent, false))
    }

    override fun getItemCount() = jobs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]

        holder.apply {
            positionTitle.text = job.title
            description.text = job.description

            Glide.with(companyLogo.context)
                .load(job.company_logo)
                .placeholder(R.color.colorPrimary)
                .fitCenter()
                .into(companyLogo)
        }
    }
}
