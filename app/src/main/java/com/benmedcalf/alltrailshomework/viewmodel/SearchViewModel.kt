package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.SearchResponse
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(repository: PlacesRepository) : ViewModel() {
    val listResultLiveData: MutableLiveData<List<SearchResultType.ListResult>> by lazy {
        MutableLiveData<List<SearchResultType.ListResult>>()
    }

    val searchResultFlow: Flow<SearchResponse> = flow {
        emit(repository.loadSearchResultsFor(50000, "-34.0,151.0"))
    }

    fun updateSearchResultsFor(
        searchResultType: SearchResultType,
        radius: Int,
        latLng: String,
        type: String = "restaurant"
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GooglePlacesService.instance.searchPlaces(radius, latLng, type)
            withContext(Dispatchers.Main) {

                when (searchResultType) {
                    is SearchResultType.MapResult -> {
                        if (request.isSuccessful) {
                            request.body()?.let { updateResultsForMap(it.results) }
                        } else {
                            TODO("handle search error for map")
                        }
                    }
                    is SearchResultType.ListResult -> {
                        if (request.isSuccessful) {
                            request.body()?.let { updateResultsForList(it.results) }
                        } else {
                            TODO("handle search error for list")
                        }
                    }
                }
            }
        }
    }

    private fun updateResultsForList(searchResults: Collection<Result>) {
        TODO("Implement Results for List update")
    }

    private fun updateResultsForMap(searchResults: Collection<Result>) {

    }

    sealed class SearchResultType {
        data class MapResult(
            val result: SearchResponse,
            val markerOptions: MarkerOptions
        ) : SearchResultType()

        data class ListResult(val result: Result) : SearchResultType()
    }

    class SearchViewModelFactory(private val repository: PlacesRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}