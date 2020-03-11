package com.dangerfield.gitjob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.dangerfield.gitjob.ui.FiltersModal
import com.dangerfield.gitjob.util.BottomNavigationManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation
            .setOnNavigationItemSelectedListener(
                BottomNavigationManager(this, R.id.content).itemSelectedListener
            )


    }
}
