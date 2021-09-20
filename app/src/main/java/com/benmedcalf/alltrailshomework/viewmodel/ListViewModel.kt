package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    // private backing val
    private val _uiState = MutableStateFlow<ListUIState>(ListUIState.Loading())
    val uiState: StateFlow<ListUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListUIState.Loading()
    )

    init {
        viewModelScope.launch {
            repository.searchResponseFlow.collect {
                val newState = ListUIState.Success(it)
                _uiState.value = newState
            }
        }
    }
}

sealed class ListUIState(val result: PlacesRepository.Result?) {
    class Loading : ListUIState(null)
    class Error : ListUIState(null)
    class Success(private val response: PlacesRepository.Result) : ListUIState(response)
}