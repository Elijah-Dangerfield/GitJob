package com.dangerfield.gitjob.ui


import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.util.hasLocationPermission
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.fragment_jobs.*

class JobsFragment: Fragment(R.layout.fragment_jobs), FilterSetter {

    private val jobsAdapter: JobsAdapter by lazy {JobsAdapter(context!!)}

    private val jobsViewModel : JobsViewModel by viewModels()

    private val filterSheet by lazy { {FiltersModal.newInstance(this)}()}


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_jobs.layoutManager = LinearLayoutManager(context)
        rv_jobs.adapter = jobsAdapter

        btn_filter.setOnClickListener { filterSheet.show(parentFragmentManager, "filters")}
        getListingsInCurrentLocation()
        setupRefresher()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("Elijah", "on destroy called")
    }

    override fun onSetFilter(filter: String?) {
        filterSheet.dismiss()
    }

    private fun getListingsInCurrentLocation() {
        if(hasLocationPermission(context!!)){
            val locationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
            locationClient.lastLocation.addOnCompleteListener {
                if(it.isSuccessful){
                    getCity(it.result!!)
                }else {
                    //deal with error, likely just show results for last thing in database
                }
            }
        }else{
            //deal with it (dont request again tho)
        }

    }

    private fun getCity(location: Location){
        Repository.getCity(location).observe(this, Observer {
            when(it){
                is Resource.Success -> {
                    tv_location.text = it.data ?: "GitJob"
                    observeListingsForCity(it.data)
                }
                is Resource.Loading -> Log.d("Elijah", "Loading city")
                is Resource.Error -> Log.d("Elijah", "Got City Error: " + it.message)
            }
        })
    }

    private fun observeListingsForCity(city: String?){

        jobsViewModel.getJobs(city).observe(viewLifecycleOwner, Observer {
            swipe_refresh_layout.isRefreshing = (it is Resource.Loading)

            when (it) {
                is Resource.Success -> {jobsAdapter.jobs = it.data ?: listOf()
                    Log.d("Elijah", "done loading with result size: " + it.data?.size )
                }
                is Resource.Error -> Log.d("Elijah", "error: " + it.message)
            }
        })
    }

    private fun setupRefresher() {
        swipe_refresh_layout.setColorSchemeResources( R.color.colorPrimary, android.R.color.holo_blue_light
            , android.R.color.holo_blue_dark)

        swipe_refresh_layout.setOnRefreshListener { jobsViewModel.refreshListings()}
    }

}
