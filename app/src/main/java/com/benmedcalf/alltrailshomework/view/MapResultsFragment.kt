package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentMapsBinding
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.viewmodel.MapUIState
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
    private val searchViewModel: SearchViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val defaultZoomLevel = 12.0f
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap

    data class MarkerInfo(
        val isFavorite: Boolean,
        val placeId: String,
        val rating: Double,
        val ratingCount: Int,
        val title: String,
        val price: String
    )

    //region Fragment Lifecycle
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

        // In the lifecycle coroutine scope
        viewLifecycleOwner.lifecycleScope.launch {
            // When we create fragment
            viewLifecycleOwner.lifecycle.whenCreated {
                // get and subscribe to the UI state

                mapViewModel.uiState.collect { mapUIState ->
                    when (mapUIState) {
                        is MapUIState.Success -> {
                            mapUIState.value?.let { placeDetailsList ->
                                // move camera to first results
                                if (placeDetailsList.isNotEmpty()) {
                                    placeDetailsList[0].geometry.location.let { firstLocation ->
                                        moveMapTo(LatLng(firstLocation.lat, firstLocation.lng))
                                    }
                                    // add markers
                                    renderMarkers(placeDetailsList)
                                }
                            }
                        }
                        is MapUIState.Loading -> {
                            //TODO handle loading map ui
                            Toast.makeText(requireContext(), "Loading!", Toast.LENGTH_SHORT).show()
                        }
                        is MapUIState.Error -> {
                            //TODO handle error map ui
                            Toast.makeText(requireContext(), "Ooopsie!", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            //TODO: How to make this a toggle button accessible by both list and map
            binding.goToListButton.setOnClickListener {
                val action =
                    MapResultsFragmentDirections.actionMapResultsFragmentToListResultsFragment()
                navController.navigate(action)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region Map and Location Callbacks
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                moveMapToCurrentLocation()
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
            googleMap.setOnInfoWindowClickListener { marker ->
                val markerInfo = marker.tag as MarkerInfo
                val action =
                    MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(
                        markerInfo.placeId
                    )
                navController.navigate(action)
            }
            googleMap.setOnMarkerClickListener(onMarkerClickListener)

            //TODO(Only call if this is the initial map load)
            moveMapToCurrentLocation()
        }

    private val onMarkerClickListener = GoogleMap.OnMarkerClickListener { marker ->
        marker.showInfoWindow()
        return@OnMarkerClickListener false
    }
    //endregion

    //region Private Functions
    private fun renderMarkers(restaurants: List<Restaurant>) {
        googleMap.clear()
        restaurants.forEach { restaurant ->
            val latLng =
                LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)

            val markerOptions = MarkerOptions().position(latLng)
            val marker = googleMap.addMarker(markerOptions)

            var formattedPriceString = restaurant.formatPrice(restaurant.priceLevel)
            marker.tag = MarkerInfo(
                restaurant.isFavorite,
                restaurant.placeId,
                restaurant.rating,
                restaurant.userRatingsTotal,
                restaurant.name,
                formattedPriceString
            )
        }
    }

    private fun permissionGranted(permission: String): Boolean {
        return checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun moveMapToCurrentLocation() {
        if (permissionGranted(ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                moveMapTo(LatLng(lastLocation.latitude, lastLocation.longitude))
                viewLifecycleOwner.lifecycleScope.launch {
                    searchViewModel.updateSearchResults(latLng = "${lastLocation.latitude},${lastLocation.longitude}")
                }
            }
        } else {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun moveMapTo(latLng: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoomLevel))
    }
    //endregion
}
