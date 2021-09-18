package com.benmedcalf.alltrailshomework.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapViewModel : ViewModel() {

    private val defaultLatLng = LatLng(-34.0, 151.0)
    private val defaultZoomLevel = 12.0f

    val mapCameraMovement: MutableLiveData<CameraUpdate> by lazy {
        MutableLiveData<CameraUpdate>()
    }

    val currentLocationMarker: MutableLiveData<MarkerOptions> by lazy {
        MutableLiveData<MarkerOptions>()
    }

    //region Map Logic
    fun updateLocation(location: Location?) {
        //TODO("clean this up")
        if (location == null) {
            val marker = MarkerOptions().position(defaultLatLng).title("Sydney, Australia")
            val movement = CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoomLevel)
            mapCameraMovement.postValue(movement)
            currentLocationMarker.postValue(marker)
        } else {
            val latLng = LatLng(location.latitude, location.longitude)
            val marker = MarkerOptions().position(latLng).title("Current Location")
            val movement = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoomLevel)
            mapCameraMovement.postValue(movement)
            currentLocationMarker.postValue(marker)
        }
    }
}
