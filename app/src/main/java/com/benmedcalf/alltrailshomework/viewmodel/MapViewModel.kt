package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.view.MapResultsFragment
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    // private backing val
    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Loading())
    val uiState: StateFlow<MapUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapUIState.Loading()
    )

    init {
        viewModelScope.launch {
            repository.searchResponseFlow.collect {
                when (it) {
                    is PlacesRepository.Result.Success -> {
                        it.value?.let { results ->
                            val cameraUpdate =
                                CameraUpdateFactory.newLatLngZoom(repository.userLocation, 12.0f)

                            var markers =
                                mutableListOf<Pair<MarkerOptions, MapResultsFragment.MarkerInfo>>()

                            results.forEach { result ->
                                val latLng =
                                    LatLng(
                                        result.geometry.location.lat, result.geometry.location.lng
                                    )

                                val formattedPriceString =
                                    result.formatPrice(result.priceLevel)
                                val formattedRatingsCount = "(${result.userRatingsTotal})"

                                val markersOptions =
                                    MarkerOptions().position(latLng).title(result.name)
                                val markerInfo = MapResultsFragment.MarkerInfo(
                                    result.isFavorite,
                                    result.placeId,
                                    result.rating,
                                    formattedRatingsCount,
                                    result.name,
                                    formattedPriceString
                                )
                                markers.add(Pair(markersOptions, markerInfo))
                            }

                            val screen = Screen(
                                cameraMovement = cameraUpdate,
                                markers = markers.toList()
                            )
                            val newState = MapUIState.Success(screen)
                            _uiState.value = newState
                        }
                    }
                    is PlacesRepository.Result.Failure -> {
                        //TODO: Result.Failure is of type List<Restaurant>. Revisit
                        it.value?.let {
                            val errorState = MapUIState.Error(error = "Oops, an error occurred")
                            _uiState.value = errorState
                        }
                    }
                    is PlacesRepository.Result.Loading -> {
                        val loadingState = MapUIState.Loading()
                        _uiState.value = loadingState
                    }
                }
            }
        }
    }
}


data class Screen(
    val cameraMovement: CameraUpdate,
    val markers: List<Pair<MarkerOptions, MapResultsFragment.MarkerInfo>>
)

sealed class MapUIState(
    val data: Screen? = null,
    val message: String? = null
) {
    class Loading : MapUIState(null, null)
    class Success(screen: Screen) :
        MapUIState(screen)

    class Error(error: String) : MapUIState(message = error)
}

