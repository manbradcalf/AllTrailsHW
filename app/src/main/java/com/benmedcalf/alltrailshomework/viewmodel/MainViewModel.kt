package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    suspend fun updateSearchResults(query: String) {
        repository.loadSearchResultsFor(query)
    }

    fun filterSearchResults() {
        //TODO("implement")
    }
}