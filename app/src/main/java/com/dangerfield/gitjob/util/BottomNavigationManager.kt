package com.dangerfield.gitjob.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.ui.jobs.feed.JobsFragment
import com.dangerfield.gitjob.ui.saved.SavedJobsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationManager(val activity: AppCompatActivity, val container: Int) {

    private val jobsFragment = JobsFragment()
    private val savedJobsFragment = SavedJobsFragment()
    private val fm = activity.supportFragmentManager
    private var activeFragment: Fragment = jobsFragment

    val itemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { selected ->
            when(selected.itemId) {
                R.id.jobsFragment -> {
                    fm.beginTransaction().hide(activeFragment).show(jobsFragment).commit()
                    activeFragment = jobsFragment
                    true
                }

                R.id.savedJobsFragment -> {
                    fm.beginTransaction().hide(activeFragment).show(savedJobsFragment).commit()
                    activeFragment = savedJobsFragment
                    true
                }
                else -> false
            }
        }

    init {
        fm.beginTransaction().add(container, savedJobsFragment).hide(savedJobsFragment).commit()
        fm.beginTransaction().add(container, jobsFragment).commit()
    }



}