package com.benmedcalf.alltrailshomework.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.SearchResponse
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(repository: PlacesRepository) : ViewModel() {
    private val defaultLatLng = LatLng(-34.0, 151.0)
    private val defaultZoomLevel = 12.0f
    private val defaultRadius = 50000

    // private backing val
    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Loading())
    val uiState: StateFlow<MapUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapUIState.Loading()
    )

    val mapCameraMovement: MutableLiveData<CameraUpdate> by lazy {
        MutableLiveData<CameraUpdate>()
    }

    val currentLocationMarker: MutableLiveData<MarkerOptions> by lazy {
        MutableLiveData<MarkerOptions>()
    }

    init {
        viewModelScope.launch {
            repository.searchResponseFlow.collect {
                val newState = MapUIState.Success(it)
                _uiState.value = newState
            }
        }
    }

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


sealed class MapUIState(val searchResponse: SearchResponse?) {
    class Loading : MapUIState(null)
    class Error : MapUIState(null)
    class Success(val response: SearchResponse) : MapUIState(response)
}

