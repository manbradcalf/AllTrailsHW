package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    sealed class SearchResultType {
        data class MapResult(
            var result: Result? = null,
            var markerOptions: MarkerOptions? = null,
            var error: String? = null
        ) :
            SearchResultType()

        data class ListResult(val result: Result?, val error: String) : SearchResultType()
    }

    companion object {
        val defaultLatLng = LatLng(-34.0, 151.0)
        const val defaultZoomLevel = 12.0f
        const val defaultRadius = 50000
    }

    val mapCameraMovement: MutableLiveData<CameraUpdate> by lazy {
        MutableLiveData<CameraUpdate>()
    }

    val currentLocationMarker: MutableLiveData<MarkerOptions> by lazy {
        MutableLiveData<MarkerOptions>()
    }

    val mapResultsLiveData: MutableLiveData<List<SearchResultType.MapResult>> by lazy {
        MutableLiveData<List<SearchResultType.MapResult>>()
    }

    val listResultLiveData: MutableLiveData<List<SearchResultType.ListResult>> by lazy {
        MutableLiveData<List<SearchResultType.ListResult>>()
    }

    fun loadSearchResultsFor(
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
        val tmpMapResults = mutableListOf<SearchResultType.MapResult>()
        searchResults.forEach { place ->
            val marker =
                MarkerOptions()
                    .position(
                        LatLng(
                            place.geometry.location.lat,
                            place.geometry.location.lng
                        )
                    )
                    .title(place.name)

            tmpMapResults.add(SearchResultType.MapResult(place, marker))
            mapResultsLiveData.postValue(tmpMapResults)
        }

    }
}
