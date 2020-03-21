package com.dangerfield.gitjob.ui.jobs.location

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.AddedLocation
import kotlinx.android.synthetic.main.item_searched_location.view.*


class AddedLocationsAdapter(private val context: Context, val persister: SearchedLocationPersister,
                            var currentDeterminedLocation: AddedLocation? = null,
                            var currentSelectedLocation: AddedLocation? = null,
                            val onSelectTerm: ((location: String) -> Unit)
                            )
    : RecyclerView.Adapter<AddedLocationsAdapter.ViewHolder>() {

    val currentLocationString = "Current Location"
    var searches = arrayListOf<AddedLocation>()
        set(value) {
            if (currentDeterminedLocation != null) value.add(AddedLocation(currentLocationString))
            field = value
            notifyDataSetChanged()
        }

    var selectedLocation: AddedLocation? = currentSelectedLocation
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTerm: TextView = view.tv_searched_location
        val currentLocationIcon: ImageView = view.iv_current_location
        val searchIcon: ImageView = view.iv_clock
        val subText: TextView = view.tv_location_subtext
        val greenCheckIcon: ImageButton = view.ib_green_check
        private val termButton: View = view.btn_location

        init {
            termButton.setOnClickListener {
                Log.d("Elijah", "Clicked!!!")
                val location = searches[adapterPosition]
                onSelectTerm(location.location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_searched_location,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = searches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = searches[position]
        holder.searchTerm.text = location.location

        when (location.location) {
            currentLocationString -> showDeterminedLocationView(holder)
            else -> {
                holder.currentLocationIcon.visibility = View.INVISIBLE
                holder.searchIcon.visibility = View.VISIBLE
                holder.subText.visibility = View.GONE
            }
        }

        holder.greenCheckIcon.visibility =  if(location == selectedLocation) View.VISIBLE else View.GONE
    }



    private fun showDeterminedLocationView(
        holder: ViewHolder
    ) {
        holder.currentLocationIcon.visibility = View.VISIBLE
        holder.searchIcon.visibility = View.INVISIBLE
        holder.subText.visibility = View.VISIBLE
        holder.subText.text = currentDeterminedLocation?.location
    }
}


