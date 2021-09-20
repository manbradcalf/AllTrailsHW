package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentMapsBinding
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.benmedcalf.alltrailshomework.viewmodel.MapViewModel
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapResultsFragment : BaseFragment() {
    private val mapViewModel: MapViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private lateinit var navController: NavController
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var currentLocation: Location? = null

    //region Fragment Lifecycle and BaseFragment Impl
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        navController = findNavController()
        mapFragment?.getMapAsync(mapReadyCallback)

        if (savedInstanceState == null) {
            checkLocationPermissions()
        }
        // In the lifecycle coroutine scope
        viewLifecycleOwner.lifecycleScope.launch {
            // When we create fragment
            viewLifecycleOwner.lifecycle.whenCreated {
                // get and subscribe to the UI state
                mapViewModel.uiState.collect { mapUIState ->
                    mapUIState.placesRepositoryResult?.value?.results?.forEach { renderMarker(it) }
                }
            }
        }

        //TODO: How to make this a toggle button accessible by both list and map
        binding.mapbtn.setOnClickListener {
            val action =
                MapResultsFragmentDirections.actionMapResultsFragmentToListResultsFragment()
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderMarker(place: Result) {
        val latLng =
            LatLng(place.geometry.location.lat, place.geometry.location.lng)

        val markerOptions = MarkerOptions().position(latLng)
        val marker = googleMap.addMarker(markerOptions)

        var formattedPriceString = ""
        repeat(place.priceLevel) {
            formattedPriceString += "$"
        }

        marker.tag = MarkerInfo(
            place.placeId,
            place.rating,
            place.userRatingsTotal,
            place.name,
            formattedPriceString
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // No need for action, as we've registered a callback on the fusedLocationListener
            } else {
                if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                    // TODO("beg for permission")
                }
            }
        }

    private val mapReadyCallback =
        OnMapReadyCallback {
            googleMap = it
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    return renderView(marker)
                }

                private fun renderView(marker: Marker): View {
                    val markerInfo = marker.tag as MarkerInfo
                    val customView = layoutInflater.inflate(
                        R.layout.info_window, null
                    )
                    val nameTextView: TextView = customView.findViewById(R.id.info_bubble_name)
                    nameTextView.text = markerInfo.title
                    val reviewsCountView: TextView =
                        customView.findViewById(R.id.rating_count)
                    reviewsCountView.text = markerInfo.ratingCount.toString()
                    val ratingView: RatingBar = customView.findViewById(R.id.rating)
                    ratingView.numStars = markerInfo.rating.toInt()

                    return customView
                }
            })
            checkLocationPermissions()
            googleMap.setOnInfoWindowClickListener { marker ->
                val markerInfo = marker.tag as MarkerInfo
                val action =
                    MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(markerInfo.placeId)
                navController.navigate(action)
            }
        }

    private val onMarkerClickListener = GoogleMap.OnMarkerClickListener { marker ->
        marker.showInfoWindow()
        return@OnMarkerClickListener false
    }


    //TODO("this function just looks ugly")
    private fun checkLocationPermissions() {
        if (checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != currentLocation) {
                    if (currentLocation == null) {
                        currentLocation = lastLocation
                    }
                    // only move camera if the location is updated
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            ), 12.0f
                        )
                    )
                    val params =
                        PlacesRepository.SearchParameters(latLng = "${lastLocation.latitude},${lastLocation.longitude}")

                    //TODO("This feels risky")
                    viewLifecycleOwner.lifecycleScope.launch {
                        searchViewModel.updateSearchResults(params)
                    }
                }
            }
        } else {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    data class MarkerInfo(
        val placeId: String,
        val rating: Double,
        val ratingCount: Int,
        val title: String,
        val price: String
    )
}
