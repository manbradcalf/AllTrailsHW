package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.RepoSearchResults
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: PlacesRepository) :
    BaseViewModel<ScreenUpdate>() {
    init {
        viewModelScope.launch {
            repository.searchResults.collect {
                when (it) {
                    is RepoSearchResults.Success -> {
                        it.searchResults?.let { results ->
                            val cameraUpdate =
                                CameraUpdateFactory.newLatLngZoom(repository.userLocation, 12.0f)

                            var markers =
                                mutableListOf<Pair<MarkerOptions, Restaurant>>()

                            results.forEach { result ->
                                val latLng =
                                    LatLng(
                                        result.geometry.location.lat,
                                        result.geometry.location.lng
                                    )
                                val markersOptions =
                                    MarkerOptions().position(latLng).title(result.name)
                                markers.add(Pair(markersOptions, result))
                            }

                            val screen = ScreenUpdate(
                                cameraMovement = cameraUpdate,
                                markers = markers.toList()
                            )
                            val newState = UIState.Success(screen)
                            _uiState.value = newState
                        }
                    }
                    is RepoSearchResults.Error -> {
                        //TODO: Result.Failure is of type List<Restaurant>. Revisit
                        it.searchResults?.let {
                            val errorState =
                                UIState.Error<ScreenUpdate>(errorMessage = "Oops, an error occurred")
                            _uiState.value = errorState
                        }
                    }
                    is RepoSearchResults.Loading -> {
                        val loadingState = UIState.Loading<ScreenUpdate>()
                        _uiState.value = loadingState
                    }
                }
            }
        }
    }
}

data class ScreenUpdate(
    val cameraMovement: CameraUpdate,
    val markers: List<Pair<MarkerOptions, Restaurant>>
)

