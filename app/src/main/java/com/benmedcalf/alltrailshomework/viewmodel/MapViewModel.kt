package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.RepoResponse
import com.benmedcalf.alltrailshomework.model.Repository
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
class MapViewModel @Inject constructor(private val repository: Repository) :
    BaseViewModel<ScreenUpdate>() {
    init {
        viewModelScope.launch {
            repository.searchResults.collect { repoSearchResults ->
                when (repoSearchResults) {
                    is RepoResponse.Success -> {
                        repoSearchResults.searchResults?.let { results ->
                            val latLng =
                                LatLng(
                                    repository.userLocation.latitude,
                                    repository.userLocation.longitude
                                )
                            val newState = UIState.Success(
                                mapResultsToScreenState(
                                    results, latLng
                                )
                            )
                            _uiState.value = newState
                        }
                    }
                    is RepoResponse.Error -> {
                        repoSearchResults.searchResults?.let {
                            val errorState =
                                UIState.Error<ScreenUpdate>(errorMessage = "Oops, an error occurred")
                            _uiState.value = errorState
                        }
                    }
                    is RepoResponse.Loading -> {
                        val loadingState = UIState.Loading<ScreenUpdate>()
                        _uiState.value = loadingState
                    }
                }
            }
        }
    }

    private fun mapResultsToScreenState(
        searchResults: List<Restaurant>,
        latLng: LatLng
    ): ScreenUpdate {
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(
                latLng, 12.0f
            )

        val markers =
            mutableListOf<Pair<MarkerOptions, Restaurant>>()

        searchResults.forEach { result ->
            val latLng =
                LatLng(
                    result.geometry.location.lat,
                    result.geometry.location.lng
                )

            val markersOptions =
                MarkerOptions()
                    .position(latLng)
                    .title(result.name)

            markers.add(Pair(markersOptions, result))
        }

        return ScreenUpdate(
            cameraMovement = cameraUpdate,
            markers = markers.toList()
        )
    }
}

class ScreenUpdate(
    val cameraMovement: CameraUpdate,
    val markers: List<Pair<MarkerOptions, Restaurant>>
)

