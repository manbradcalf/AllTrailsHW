package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    suspend fun updateSearchResults(
        queryText: String? = "",
        latLng: String? = "38.7653,-77.1024",
        radius: Int? = 5000,
        type: String? = "restaurant"
    ) {
        repository.loadSearchResultsFor(
            PlacesRepository.SearchParameters(
                name = queryText!!,
                latLng = latLng!!,
                radius = radius!!,
                type = type!!
            )
        )
    }

    fun filterSearchResults() {
        //TODO("implement")
    }
}