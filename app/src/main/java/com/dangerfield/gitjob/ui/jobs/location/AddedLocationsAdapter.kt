package com.dangerfield.gitjob.ui.jobs.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.AddedLocation
import kotlinx.android.synthetic.main.item_searched_location.view.*


class AddedLocationsAdapter(private val context: Context, val persister: SearchedLocationPersister, val onSelectTerm: ((location: String) -> Unit)): RecyclerView.Adapter<AddedLocationsAdapter.ViewHolder>() {

    var searches = listOf<AddedLocation>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTerm: TextView = view.tv_searched_location
        private val removeButton: ImageButton = view.ib_remove
        private val termButton: View = view.btn_location

        init {
            removeButton.setOnClickListener {
                val location = searches[adapterPosition]
                persister.removeSearchedLocation(location)
            }

            termButton.setOnClickListener {
                val location = searches[adapterPosition]
                onSelectTerm(location.location)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_searched_location, parent, false))
    }

    override fun getItemCount() = searches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchTerm.text = searches[position].location
    }
}