package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel<T> : ViewModel() {
    val TAG = "${this.javaClass::class}"
    val _uiState = MutableStateFlow<UIState<T>>(UIState.Loading())
    val uiState: StateFlow<UIState<T>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UIState.Loading()
    )

    sealed class UIState<T>(
        val data: T? = null,
        val status: String
    ) {
        class Success<T>(var value: T) : UIState<T>(data = value, status="Success!")
        class Error<T>(errorMessage: String) : UIState<T>(status = errorMessage)
        class Loading<T> : UIState<T>(null, status = "loading")
    }
}