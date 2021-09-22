package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentMapsBinding
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.viewmodel.BaseViewModel
import com.benmedcalf.alltrailshomework.viewmodel.MapViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapResultsFragment : Fragment() {
    private val mapViewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap

    //region Fragment Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.loadingIndicatorMaps.visibility = View.VISIBLE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        navController = findNavController()
        mapFragment?.getMapAsync(mapReadyCallback)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.uiState.collect { mapUIState ->
                    when (mapUIState) {
                        is BaseViewModel.UIState.Success -> {
                            binding.loadingIndicatorMaps.visibility = View.GONE
                            if (mapUIState.data != null) {
                                renderMapWithResults(
                                    mapUIState.data.cameraMovement,
                                    mapUIState.data.markers
                                )
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No results! Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is BaseViewModel.UIState.Loading -> {
                            binding.loadingIndicatorMaps.visibility = View.VISIBLE
                        }
                        is BaseViewModel.UIState.Error -> {
                            binding.loadingIndicatorMaps.visibility = View.GONE
                            Log.e("MapResultsFragment", "An error occurred: ${mapUIState.status}")
                        }
                    }
                }
            }
        }

        binding.goToListButton.setOnClickListener {
            val action =
                MapResultsFragmentDirections.actionMapResultsFragmentToListResultsFragment()
            navController.navigate(action)
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
        val restaurant = marker.tag as Restaurant
        val action =
            MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(restaurant)
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
            val restaurant = marker.tag as Restaurant
            val infoWindowView = layoutInflater.inflate(
                R.layout.info_window, null
            )
            val nameTextView: TextView = infoWindowView.findViewById(R.id.info_bubble_name)
            nameTextView.text = restaurant.name
            val reviewsCountView: TextView =
                infoWindowView.findViewById(R.id.rating_count)
            reviewsCountView.text = restaurant.userRatingsTotal.toString()
            val ratingView: RatingBar = infoWindowView.findViewById(R.id.rating)
            ratingView.numStars = restaurant.rating.toInt()

            return infoWindowView
        }
    }
    //endregion

    //region Private Functions
    private fun renderMapWithResults(
        cameraMovement: CameraUpdate?,
        markers: List<Pair<MarkerOptions, Restaurant>>,
    ) {
        googleMap.clear()
        cameraMovement?.let { googleMap.moveCamera(it) }
        markers.forEach { optionsAndInfo ->
            val markerOptions = optionsAndInfo.first
            val restaurantInfo = optionsAndInfo.second

            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = restaurantInfo
        }
    }
//endregion
}