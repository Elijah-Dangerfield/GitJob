package com.dangerfield.gitjob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dangerfield.gitjob.util.BottomNavigationManager
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
