package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentMapsBinding
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
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val mapReadyCallback =
        OnMapReadyCallback {
            googleMap = it
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View? {
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
        }

    private val onMarkerClickListener =
        GoogleMap.OnMarkerClickListener { marker ->
            marker.showInfoWindow()
            return@OnMarkerClickListener false
        }

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
        mapFragment?.getMapAsync(mapReadyCallback)
        if (savedInstanceState == null) {
            checkLocationPermissions()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenCreated {
                mapViewModel.uiState.collect {
                    it.searchResponse?.results?.forEach { place ->
                        val latLng =
                            LatLng(place.geometry.location.lat, place.geometry.location.lng)

                        val markerOptions = MarkerOptions().position(latLng)
                        val movement = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f)
                        googleMap.moveCamera(movement)
                        val marker = googleMap.addMarker(markerOptions)
                        marker.tag = MarkerInfo(
                            place.rating,
                            place.userRatingsTotal,
                            place.name,
                            place.priceLevel
                        )
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkLocationPermissions() {
        if (checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // set location listeners
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    searchViewModel.updateSearchResultsFor(
                        SearchViewModel.SearchResultType.MapResult(),
                        50000,
                        "${lastLocation.latitude},${lastLocation.longitude}"
                    )
                    mapViewModel.updateLocation(lastLocation)
                } else {
                    TODO()
                }
            }
        } else {
            shouldShowRequestPermissionRationale("Please....")
        }
    }

    data class MarkerInfo(
        val rating: Double,
        val ratingCount: Int,
        val title: String,
        val price: Int
    ) {
        fun formatPrice(dollarSigns: Int): String {
            var priceString = ""
            repeat(dollarSigns) {
                priceString += "$"
            }
            return priceString
        }
    }


}
