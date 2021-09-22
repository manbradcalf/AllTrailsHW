package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun updateSearchResults(query: String) {
        repository.searchNearby(query)
    }
}