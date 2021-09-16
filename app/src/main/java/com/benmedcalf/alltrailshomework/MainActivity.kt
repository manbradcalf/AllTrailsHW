package com.benmedcalf.alltrailshomework

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.benmedcalf.alltrailshomework.databinding.ActivityMainBinding
import com.benmedcalf.alltrailshomework.view.MapResultsFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.toggleViewFab.setOnClickListener {
            //TODO: How to make this toggle button aware which fragment is active
            val placeId = "ChIJN1t_tDeuEmsRUsoyG83frY4"
            val action =
                MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(placeId)
            navController.navigate(action)
        }

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)

        val key = ai.metaData["com.google.android.geo.API_KEY"].toString()
        Toast.makeText(applicationContext, key, Toast.LENGTH_LONG).show()
    }
}