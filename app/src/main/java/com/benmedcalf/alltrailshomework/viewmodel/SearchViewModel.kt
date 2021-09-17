package com.benmedcalf.alltrailshomework.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Implement the ViewModel
class SearchViewModel : ViewModel() {
    private val defaultLatLng = LatLng(-34.0, 151.0)
    private val defaultZoomLevel = 12.0f

    val placesDetailResponse: MutableLiveData<PlaceDetailsResponse> by lazy {
        MutableLiveData<PlaceDetailsResponse>()
    }

    val mapCameraMovement: MutableLiveData<CameraUpdate> by lazy {
        MutableLiveData<CameraUpdate>()
    }

    val currentLocationMarker: MutableLiveData<MarkerOptions> by lazy {
        MutableLiveData<MarkerOptions>()
    }

    val searchResultsMarkers: MutableLiveData<List<MarkerOptions>> by lazy {
        MutableLiveData<List<MarkerOptions>>()
    }

    fun loadSearchResults(radius: Int, latLng: LatLng) {
        CoroutineScope(Dispatchers.IO).launch {
            val locationParameter = "${latLng.latitude},${latLng.longitude}"
            val request = GooglePlacesService.instance.searchPlaces(radius, locationParameter)
            withContext(Dispatchers.Main) {
                if (request.isSuccessful) {
                    val listOfResultsMarkers = mutableListOf<MarkerOptions>()
                    request.body()?.results?.forEach {
                        listOfResultsMarkers.add(
                            MarkerOptions().position(
                                LatLng(
                                    it.geometry.location.lat,
                                    it.geometry.location.lng
                                )
                            ).title(it.name)
                        )
                    }
                    searchResultsMarkers.postValue(listOfResultsMarkers)
                }
            }
        }
    }

    //region Map Logic
    fun updateCurrentLocation(location: Location?) {
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
