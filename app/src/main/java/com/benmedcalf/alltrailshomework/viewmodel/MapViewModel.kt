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
                when (it) {
                    is PlacesRepository.PlacesRepositoryResult.Success -> {
                        val newState = MapUIState.Success(it)
                        _uiState.value = newState
                    }
                    is PlacesRepository.PlacesRepositoryResult.Failure -> {
                        val errorState = MapUIState.Error(it)
                        _uiState.value = errorState
                    }
                    is PlacesRepository.PlacesRepositoryResult.Failure -> {
                        val loadingState = MapUIState.Loading()
                        _uiState.value = loadingState
                    }
                }
            }
        }
    }
}

sealed class MapUIState(val result: PlacesRepository.PlacesRepositoryResult) {
    class Loading() : MapUIState(PlacesRepository.PlacesRepositoryResult.Loading())
    class Error(result: PlacesRepository.PlacesRepositoryResult.Failure) : MapUIState(result)
    class Success(result: PlacesRepository.PlacesRepositoryResult.Success) : MapUIState(result)
}

