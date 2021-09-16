package com.benmedcalf.alltrailshomework

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.benmedcalf.alltrailshomework.databinding.ActivityMainBinding
import com.benmedcalf.alltrailshomework.view.MapResultsFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.toggleViewFab.setOnClickListener {
            val placeId = "abc123"
            val action =
                MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(placeId)
            navController.navigate(action)
        }

    }
}