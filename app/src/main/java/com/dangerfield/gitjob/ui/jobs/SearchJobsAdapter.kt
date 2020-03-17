package com.dangerfield.gitjob.ui.jobs


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SearchedTerm
import kotlinx.android.synthetic.main.item_search.view.*

class SearchJobsAdapter(private val context: Context, val persister: SearchTermPersister, val onSelectTerm: ((term: String) -> Unit)): RecyclerView.Adapter<SearchJobsAdapter.ViewHolder>() {

    var searches = listOf<SearchedTerm>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTerm: TextView = view.tv_search_term
        private val removeButton: ImageButton = view.ib_remove
        private val termButton: View = view.btn_term

        init {
            removeButton.setOnClickListener {
                val search = searches[adapterPosition]
                persister.removeSearchTerm(search)
            }

            termButton.setOnClickListener {
                val search = searches[adapterPosition]
                onSelectTerm(search.term)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search, parent, false))
    }

    override fun getItemCount() = searches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchTerm.text = searches[position].term
    }
}
