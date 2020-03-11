package com.dangerfield.gitjob

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.dangerfield.gitjob.api.Repository
import com.dangerfield.gitjob.api.Resource
import com.dangerfield.gitjob.ui.FiltersModal
import com.dangerfield.gitjob.util.BottomNavigationManager
import com.dangerfield.gitjob.util.hasLocationPermission
import com.dangerfield.gitjob.util.requestLocationPermission
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION = 1998

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation
            .setOnNavigationItemSelectedListener(
                BottomNavigationManager(this, R.id.content).itemSelectedListener
            )

        checkLocationPermission()

    }

    private fun checkLocationPermission() {
        if(!hasLocationPermission(this)){
            requestLocationPermission(LOCATION_PERMISSION)
        }
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
            } else {
                Log.d("Elijah", "Got permission denied")
            }
        }
    }
}
