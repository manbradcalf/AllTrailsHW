package com.benmedcalf.alltrailshomework.view

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapResultsFragment : Fragment() {
    private val viewmodel: SearchViewModel by activityViewModels()
    private var location: Location? = null
    private val callback = OnMapReadyCallback { googleMap ->
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(
                MarkerOptions().position(latLng)
                    .title("Your Location")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.moveCamera(CameraUpdateFactory.zoomBy(10.0f))
        }
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }
}