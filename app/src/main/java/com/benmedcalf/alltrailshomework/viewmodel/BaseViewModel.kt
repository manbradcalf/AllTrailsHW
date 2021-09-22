package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

abstract class BaseViewModel<T> : ViewModel() {
    val _uiState = MutableStateFlow<UIState<T>>(UIState.Loading())
    val uiState: StateFlow<UIState<T>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UIState.Loading()
    )

    sealed class UIState<T>(
        val data: T? = null,
        val message: String? = null
    ) {
        class Success<T>(var value: T) : UIState<T>(data = value)
        class Error<T>(errorMessage: String) : UIState<T>(message = errorMessage)
        class Loading<T> : UIState<T>(null, null)
    }
}