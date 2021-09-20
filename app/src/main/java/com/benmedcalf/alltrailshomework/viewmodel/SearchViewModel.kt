package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    suspend fun updateSearchResults(params: PlacesRepository.SearchParameters) {
        repository.loadSearchResultsFor(params)
    }
}