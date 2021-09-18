package com.benmedcalf.alltrailshomework.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.DetailFragmentBinding
import com.benmedcalf.alltrailshomework.viewmodel.MapViewModel
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel.SearchResultType.MapResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapResultsFragment : BaseFragment() {
    private lateinit var googleMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private var _binding: DetailFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    // TODO("Do I even need binding and _binding here?")
    private val binding get() = _binding!!

    //region Map and Permission handlers
    private val mapReadyCallback =
        OnMapReadyCallback {
            googleMap = it
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            checkPermission()
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
    //endregion


    //region Fragment Lifecycle and BaseFragment Impl
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailFragmentBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        setObservers()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)
        updateLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setObservers() {
        mapViewModel.currentLocationMarker
            .observe(viewLifecycleOwner, { googleMap.addMarker(it) })

        mapViewModel.mapCameraMovement
            .observe(viewLifecycleOwner, { googleMap.moveCamera(it) })

        searchViewModel.mapResultsLiveData
            .observe(viewLifecycleOwner, { mapResults ->
                mapResults.forEach { mapResult ->
                    val marker = googleMap.addMarker(mapResult.markerOptions)
                    marker.tag = mapResult.result?.reference
                }
            })
    }
    //endregion

    private fun checkPermission() {
        if (checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            updateLocation()
        } else {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    //TODO("revisit this")
    fun updateLocation() {
        if (checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    val latLngQueryParam = "${lastLocation.latitude},${lastLocation.longitude}"
                    val radius = SearchViewModel.defaultRadius

                    mapViewModel.updateLocation(lastLocation)
                    searchViewModel.loadSearchResultsFor(
                        MapResult(), radius, latLngQueryParam
                    )
                } else {
                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null
                    )
                }
            }
        }
    }
}
