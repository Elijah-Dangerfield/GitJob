package com.dangerfield.gitjob.ui.jobs.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.gitjob.R
import kotlinx.android.synthetic.main.item_autocomplete.view.*

class AutoCompleteAdapter(private val context: Context, val onSelectTerm: ((term: String) -> Unit)): RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {

    var terms = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTerm: TextView = view.tv_search_term
        val searchButton: View = view.btn_term

        init {

            searchButton.setOnClickListener {
                val term = terms[adapterPosition]
                onSelectTerm(term)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_autocomplete, parent, false))
    }

    override fun getItemCount() = terms.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchTerm.text = terms[position]
    }
}