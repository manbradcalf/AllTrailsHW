package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
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
                val newState = MapUIState.Success(it)
                _uiState.value = newState
            }
        }
    }
}


sealed class MapUIState(val placesRepositoryResult: PlacesRepository.PlacesRepositoryResult?) {
    class Loading : MapUIState(null)
    class Error : MapUIState(null)
    class Success(private val response: PlacesRepository.PlacesRepositoryResult) :
        MapUIState(response)
}

