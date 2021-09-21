package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
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
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap

    data class MarkerInfo(
        val isFavorite: Boolean,
        val placeId: String,
        val rating: Double,
        val ratingCount: String,
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

        if (savedInstanceState == null) {
            mapFragment?.getMapAsync(mapReadyCallback)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenCreated {
                mapViewModel.uiState.collect { mapUIState ->
                    when (mapUIState) {
                        is MapUIState.Success -> {
                            if (mapUIState.value == null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Search yielded no results",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (mapUIState.value.isNotEmpty()) {
                                    renderMarkers(mapUIState.value)
                                }
                            }
                        }
                        is MapUIState.Loading -> {
                            binding.loadingIndicator.visibility = View.VISIBLE

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

    //region Map Listeners and Adapters
    private val mapReadyCallback =
        OnMapReadyCallback {
            googleMap = it
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            googleMap.setInfoWindowAdapter(infoWindowAdapter)
            googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener)
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
        }

    private val onMarkerClickListener = GoogleMap.OnMarkerClickListener { marker ->
        marker.showInfoWindow()
        return@OnMarkerClickListener false
    }

    private val onInfoWindowClickListener = GoogleMap.OnInfoWindowClickListener { marker ->
        val markerInfo = marker.tag as MarkerInfo
        val action =
            MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(
                markerInfo.placeId
            )
        navController.navigate(action)
    }

    private val infoWindowAdapter = object : GoogleMap.InfoWindowAdapter {
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
    }
    //endregion

    //region Private Functions
    private fun renderMarkers(restaurants: List<Restaurant>) {
        googleMap.clear()
        restaurants.forEach { restaurant ->
            val latLng = LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)
            val markerOptions = MarkerOptions().position(latLng)
            val marker = googleMap.addMarker(markerOptions)
            var formattedPriceString = restaurant.formatPrice(restaurant.priceLevel)
            var formattedRatingsCount = "(${restaurant.userRatingsTotal})"
            marker.tag = MarkerInfo(
                restaurant.isFavorite,
                restaurant.placeId,
                restaurant.rating,
                formattedRatingsCount,
                restaurant.name,
                formattedPriceString
            )
        }
    }
//endregion
}
