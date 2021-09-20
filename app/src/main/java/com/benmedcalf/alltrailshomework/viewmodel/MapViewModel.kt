package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.Restaurant
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
                        it.value?.let {
                            val newState = MapUIState.Success(restaurants = it)
                            _uiState.value = newState
                        }
                    }
                    is PlacesRepository.Result.Failure -> {
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

sealed class MapUIState(val value: List<Restaurant>? = null, val message: String? = null) {
    class Loading : MapUIState(null, null)
    class Success(restaurants: List<Restaurant>) : MapUIState(value = restaurants)
    class Error(error: String) : MapUIState(message = error)
}

