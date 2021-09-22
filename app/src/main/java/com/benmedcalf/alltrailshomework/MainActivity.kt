package com.benmedcalf.alltrailshomework

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.benmedcalf.alltrailshomework.databinding.ActivityMainBinding
import com.benmedcalf.alltrailshomework.model.Repository
import com.benmedcalf.alltrailshomework.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var repo: Repository
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    // Set user location in repo for access in viewmodel layer later
                    repo.userLocation = LatLng(location.latitude, location.longitude)
                    lifecycleScope.launch {
                        mainViewModel.updateSearchResults("")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.detailFragment) {
                binding.searchFragmentContainerView.visibility = View.GONE
            } else {
                binding.searchFragmentContainerView.visibility = View.VISIBLE
            }
        }

        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }
}