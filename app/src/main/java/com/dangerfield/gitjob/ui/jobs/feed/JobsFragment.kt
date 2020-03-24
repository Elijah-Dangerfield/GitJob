package com.dangerfield.gitjob.ui.jobs.feed


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.api.GitHubErrorMessage
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.model.AddedLocation
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.ui.JobDetailFragment
import com.dangerfield.gitjob.ui.jobs.filter.FilterSetter
import com.dangerfield.gitjob.ui.jobs.filter.FiltersModal
import com.dangerfield.gitjob.ui.jobs.location.LocationChangeFragment
import com.dangerfield.gitjob.ui.jobs.search.SearchFragment
import com.dangerfield.gitjob.ui.saved.ModalOption
import com.dangerfield.gitjob.ui.saved.OptionsHandler
import com.dangerfield.gitjob.ui.saved.SavedOptionsModal
import com.dangerfield.gitjob.util.*
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_jobs.*
import kotlinx.android.synthetic.main.item_jobs_filter.view.*
import kotlinx.android.synthetic.main.toolbar_jobs.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class JobsFragment: Fragment(R.layout.fragment_jobs), FilterSetter, ListingSaver, OptionsHandler {

    private val jobsViewModel : JobsViewModel by viewModel()

    private val jobsAdapter: JobsAdapter by lazy { JobsAdapter(context!!, this) {
        navigateToJobDetail(it)
    } }

    private val savedOptionsModal by lazy { {
        SavedOptionsModal.newInstance(this)
    }()}


    private val filterSheet by lazy { {
        FiltersModal.newInstance(this, jobsViewModel.filter.value)
    }}

    private val newLocationChangeFragment
            = { determined: String?, selected: String? ->
        {
        LocationChangeFragment.newInstance(
            filterSetter = this,
            currentDeterminedLocation = determined,
            currentSelectedLocation = selected
        )
    }()}

    private val newSearchFragment =  { {
        SearchFragment.newInstance(this)
    }()}

    private val LOCATION_PERMISSION = 1998


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        requestUserCity()
        observeListings()
        observeLocation()
        observeSearchTerm()
        observeFilter()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUserCity()
            } else {
            }
        }
    }

    override fun saveListing(listing: JobListing) {
        jobsViewModel.saveListing(listing)
        val jobFound = jobsAdapter.jobs.find { job -> job.id == listing.id}
        if(jobFound != null) {
            jobFound.saved = true
            jobsAdapter.notifyDataSetChanged()
        }
    }

    override fun unsaveListing(listing: JobListing) {
        jobsViewModel.unsaveListing(listing)
        val jobFound = jobsAdapter.jobs.find { job -> job.id == listing.id}
        if(jobFound != null) {
            jobFound.saved = false
            jobsAdapter.notifyDataSetChanged()
        }
    }

    override fun onSetSearchTerm(term: String) { jobsViewModel.setSearchTerm(term) }

    override fun onSetCity(city: String?) {
        jobsViewModel.setSelectedLocation(city, refresh = true)
    }

    override fun presentOptions(savedJob: SavedJob) {
        savedOptionsModal.show(parentFragmentManager, savedJob, listOf(ModalOption.SHARE))
    }

    override fun onShare(savedJob: SavedJob) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Check out this great job I found on Git Job")
            putExtra(Intent.EXTRA_TEXT, savedJob.url!!)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)    }

    override fun onDelete(savedJob: SavedJob) {/*No Op*/}

    private fun observeLocation() {
        jobsViewModel.getSelectedLocation().observe(viewLifecycleOwner, Observer {
            Log.d("Elijah", "Got new location string: $it")

            included_toolbar.tv_location.text =
                if(it.isNullOrEmpty()) resources.getString(R.string.app_name) else it
        })
    }

    override fun onSetFilter(filter: Filter?) {
        jobsViewModel.filter.value = filter
    }

    private fun setupViews() {
        rv_jobs.layoutManager = LinearLayoutManager(context)
        rv_jobs.adapter = jobsAdapter
        included_toolbar.btn_filter.setOnClickListener { filterSheet().show(parentFragmentManager, "filter") }
        included_toolbar.location_view.setOnClickListener {
            newLocationChangeFragment(jobsViewModel.determinedLocation.value, jobsViewModel.getSelectedLocation().value).show(parentFragmentManager, "location_change")
        }

        included_toolbar.ib_search.setOnClickListener { navigateToSearch() }

        swipe_refresh_layout.setColorSchemeResources(
            R.color.colorPrimary, android.R.color.holo_blue_light
            , android.R.color.holo_blue_dark
        )

        swipe_refresh_layout.setOnRefreshListener { jobsViewModel.refreshListings()}

        included_search_term_view.ib_clear_filter.setOnClickListener {
            jobsViewModel.setSearchTerm(null)
        }

        included_filter_view.ib_clear_filter.setOnClickListener {
            jobsViewModel.filter.value = null
        }
    }
    private fun observeListings() {

        jobsViewModel.getJobs().observe(viewLifecycleOwner, Observer {
            swipe_refresh_layout.isRefreshing = (it is Resource.Loading)

            when (it) {
                is Resource.Success ->  {
                    jobsAdapter.jobs = it.data!!
                    console.log("got success in view layer with ${it.data.size} results")
                }

                is Resource.Loading ->  {
                    if(!it.data.isNullOrEmpty()) { jobsAdapter.jobs = it.data }
                }

                is Resource.Error -> {
                    console.log("got error in view")

                    if(it.errorType == GitHubErrorMessage.BAD_LOCATION) {
                        jobsViewModel.setSelectedLocation(null)
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
            included_search_term_view.goneIf(it.isNullOrEmpty())
            toolbar_search_term.goneIf(included_search_term_view.visibility == View.GONE
                    && included_filter_view.visibility == View.GONE)

            if(!it.isNullOrEmpty()) included_search_term_view.tv_filter.text = "\"$it\""
        })
    }

    private fun observeFilter() {
        jobsViewModel.filter.observe(viewLifecycleOwner, Observer {
            Log.d("Elijah", "New filter: $it")
            included_filter_view.goneIf(it == null)
            toolbar_search_term.goneIf(included_search_term_view.visibility == View.GONE
                    && included_filter_view.visibility == View.GONE)

            if(it != null) {
                included_filter_view.tv_filter.text = it.jobtype
                jobsAdapter.jobs = jobsAdapter.jobs.filter {job -> job.type == it.jobtype }
            }else {
                jobsViewModel.jobsForcePost() // update live data so observer will update adapter
            }
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
                                jobsViewModel.setSelectedLocation(response.data!!, refresh = true)
                                jobsViewModel.saveLocation(AddedLocation(response.data))
                                //TODO change this to only update if the users prefrences are user location
                                // otherwise logic elsewhere should just use the last USED location, which could be set in the done button click
                                jobsViewModel.determinedLocation.value = response.data
                            }
                            is Resource.Error ->  {
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                jobsViewModel.setSelectedLocation(null)
                                included_toolbar.tv_location.text = resources.getString(R.string.app_name)
                                Log.d("Elijah", "error getting user city: " + response.message)
                            }


                        }
                    })
                }else {
                    Log.d("Elijah", "error getting user location from android")
                    Toast.makeText(context, "Could not obtain location, showing all listings", Toast.LENGTH_LONG).show()
                    included_toolbar.tv_location.text = resources.getString(R.string.app_name)
                }
            }
        }else{
            Log.d("Elijah", "requesting user permissions: ")
            requestLocationPermission(LOCATION_PERMISSION)
        }
    }

    private fun navigateToJobDetail(job: JobListing) {
        parentFragmentManager
            .beginTransaction().add(R.id.content, JobDetailFragment.newInstance(job, this, this))
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearch() {
        Log.d("Elijah", "navigating to search")

        parentFragmentManager
            .beginTransaction().add(R.id.content, newSearchFragment())
            .addToBackStack(null)
            .commit()
    }
}
