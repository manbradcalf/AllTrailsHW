package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.findNavController
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentMapsBinding
import com.benmedcalf.alltrailshomework.viewmodel.MapViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapResultsFragment : BaseFragment() {
    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val mapReadyCallback =
        OnMapReadyCallback {
            googleMap = it
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            checkLocationPermissions()
        }

    private val onMarkerClickListener =
        GoogleMap.OnMarkerClickListener { marker ->
            marker.tag?.let {
                val placeId = it as String
                val action =
                    MapResultsFragmentDirections.actionMapResultsFragmentToDetailFragment(placeId)
                findNavController().navigate(action)
            }
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
                        val latLng = LatLng(
                            place.geometry.location.lat,
                            place.geometry.location.lng
                        )
                        val marker = MarkerOptions().position(latLng).title(place.name)
                        val movement = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f)
                        googleMap.moveCamera(movement)
                        googleMap.addMarker(marker)
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
                    TODO()
                } else {
                    TODO()
                }
            }
        } else {
            shouldShowRequestPermissionRationale("Please....")
        }
    }
}
