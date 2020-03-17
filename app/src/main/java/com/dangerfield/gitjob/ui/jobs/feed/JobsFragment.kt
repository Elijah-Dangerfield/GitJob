package com.dangerfield.gitjob.ui.jobs.feed


import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.api.GitHubErrorMessage
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.ui.jobs.filter.FilterSetter
import com.dangerfield.gitjob.ui.jobs.filter.FiltersModal
import com.dangerfield.gitjob.ui.jobs.location.LocationChangeFragment
import com.dangerfield.gitjob.ui.jobs.search.SearchFragment
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.hasLocationPermission
import com.dangerfield.gitjob.util.requestLocationPermission
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.fragment_jobs.*
import org.koin.android.viewmodel.ext.android.viewModel

class JobsFragment: Fragment(R.layout.fragment_jobs),
    FilterSetter {

    private val jobsAdapter: JobsAdapter by lazy {
        JobsAdapter(
            context!!,
            jobsViewModel
        )
    }

    private val jobsViewModel : JobsViewModel by viewModel()

    private val filterSheet by lazy { {
        FiltersModal.newInstance(
            this
        )
    }()}

    private val newLocationChangeFragment = { {
        LocationChangeFragment.newInstance(
            this
        )
    }()}

    private val newSearchFragment =  { {
        SearchFragment.newInstance(
            this
        )
    }()}

    private val LOCATION_PERMISSION = 1998


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        requestUserCity()
        observeListings()
        observeLocation()
        observeSearchTerm()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Elijah", "Got permission accepted")
                requestUserCity()
            } else {
                Log.d("Elijah", "Got permission denied")
            }
        }
    }

    override fun onSetSearchTerm(term: String) {
        jobsViewModel.setSearchTerm(term)
    }

    override fun onSetCity(city: String) {
        jobsViewModel.setLocation(city, refresh = true)
    }

    private fun observeLocation() {
        jobsViewModel.getLocation().observe(viewLifecycleOwner, Observer {
            Log.d("Elijah", "Got new location string: $it")

            tv_location.text =
                if(it.isNullOrEmpty()) resources.getString(R.string.app_name) else it
        })
    }

    override fun onSetFilter(filter: String?) { filterSheet.dismiss() }

    private fun setupViews() {
        rv_jobs.layoutManager = LinearLayoutManager(context)
        rv_jobs.adapter = jobsAdapter
        btn_filter.setOnClickListener { filterSheet.show(parentFragmentManager, "filters") }
        location_view.setOnClickListener {
            newLocationChangeFragment().show(parentFragmentManager, "location_change")
        }

        ib_search.setOnClickListener { navigateToSearch() }

        swipe_refresh_layout.setColorSchemeResources(
            R.color.colorPrimary, android.R.color.holo_blue_light
            , android.R.color.holo_blue_dark
        )

        swipe_refresh_layout.setOnRefreshListener { jobsViewModel.refreshListings()}

        ib_clear_search_term.setOnClickListener {
            jobsViewModel.setSearchTerm(null)
        }
    }
    private fun observeListings() {

        jobsViewModel.getJobs().observe(viewLifecycleOwner, Observer {
            swipe_refresh_layout.isRefreshing = (it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    Log.d("Elijah", "done loading with result size: " + it.data?.size )
                    var count = 0
                    it.data!!.forEach { if(it.saved == true) count++ }
                    Log.d("Elijah", "sending list to adapter with $count items marked as saved")

                    jobsAdapter.jobs = it.data ?: listOf()




                }
                is Resource.Loading ->  Log.d("Elijah", "loading: ")
                is Resource.Error -> {
                    Log.d("Elijah", "error: " + it.message)
                    if(it.errorType == GitHubErrorMessage.BAD_LOCATION) {
                        jobsViewModel.setLocation(null)
                    }else if (it.errorType == GitHubErrorMessage.BAD_SEARCH) {
                        jobsViewModel.setSearchTerm(null)
                    }
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun observeSearchTerm() {
        jobsViewModel.getSearchTerm().observe(viewLifecycleOwner, Observer {
            toolbar_search_term.goneIf(it.isNullOrEmpty())
            if(!it.isNullOrEmpty()) tv_search_term.text = "\"$it\""
        })
    }

    private fun requestUserCity() {
        if(hasLocationPermission(context!!)){
            val locationClient =
                LocationServices.getFusedLocationProviderClient(activity as Activity)
            locationClient.lastLocation.addOnCompleteListener {
                if(it.isSuccessful && it.result != null){

                    jobsViewModel.getUserCity(it.result!!)
                        .observe(this, Observer {response ->
                        when(response){
                            is Resource.Success -> {
                                Log.d("Elijah", "got user city: " +  response.data)
                                jobsViewModel.setLocation(response.data!!, refresh = true)
                            }
                            is Resource.Error ->  {
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                jobsViewModel.setLocation(null)
                                tv_location.text = resources.getString(R.string.app_name)
                                Log.d("Elijah", "error getting user city: " + response.message)
                            }


                        }
                    })
                }else {
                    Log.d("Elijah", "error getting user location from android")
                }
            }
        }else{
            Log.d("Elijah", "requesting user permissions: ")
            requestLocationPermission(LOCATION_PERMISSION)
        }
    }

    private fun navigateToSearch() {
        Log.d("Elijah", "navigating to search")

        parentFragmentManager
            .beginTransaction().add(R.id.content, newSearchFragment())
            .addToBackStack(null)
            .commit()
    }
}
