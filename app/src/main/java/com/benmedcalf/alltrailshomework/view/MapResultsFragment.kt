package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapResultsFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            return@registerForActivityResult permissionResponseHandler(isGranted)
        }

    private fun permissionResponseHandler(granted: Boolean) {
        if (granted) {
            updateLocation()
        } else {
            shouldShowRequestPermissionRationale("Please....")
        }
    }

    private fun updateLocation() {
        // Necessary permission check to fetch location
        if (checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    searchViewModel.updateCurrentLocation(it)
                    searchViewModel.loadSearchResults(20, LatLng(it.latitude, it.longitude))
                } else {
                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null
                    )
                }
            }
        }
    }

    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        updateLocation()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        searchViewModel.currentLocationMarker.observe(viewLifecycleOwner, { mMap.addMarker(it) })

        searchViewModel.mapCameraMovement.observe(viewLifecycleOwner, { mMap.moveCamera(it) })

        searchViewModel.searchResultsMarkers.observe(viewLifecycleOwner, {
            Toast.makeText(activity, "Got result: ${it[0].title}", Toast.LENGTH_LONG).show()
        })

        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)
    }
}