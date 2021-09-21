package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
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
                        it.value?.let { restaurants ->
                            val newLocation =
                                CameraUpdateFactory.newLatLngZoom(repository.userLocation, 12.0f)
                            val newState = MapUIState.Success(
                                restaurants = restaurants,
                                cameraMovement = newLocation
                            )
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

sealed class MapUIState(
    val value: List<Restaurant>,
    val cameraMovement: CameraUpdate? = null,
    val message: String? = null
) {
    class Loading : MapUIState(emptyList(), null)
    class Success(restaurants: List<Restaurant>, cameraMovement: CameraUpdate) :
        MapUIState(value = restaurants, cameraMovement = cameraMovement)

    class Error(error: String) : MapUIState(emptyList(), message = error)
}

