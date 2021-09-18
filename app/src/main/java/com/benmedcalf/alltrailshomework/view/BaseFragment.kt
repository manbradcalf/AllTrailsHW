package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

abstract class BaseFragment: Fragment() {
    lateinit var searchViewModel: SearchViewModel
    lateinit var fusedLocationClient: FusedLocationProviderClient

    abstract fun setObservers()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        super.onViewCreated(view, savedInstanceState)
    }
}