package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

abstract class BaseFragment : Fragment() {
    val TAG = "ViewLayer: ${this.javaClass::class}"
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        super.onViewCreated(view, savedInstanceState)
    }
}