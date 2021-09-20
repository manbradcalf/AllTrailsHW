package com.benmedcalf.alltrailshomework.model

import androidx.annotation.WorkerThread
import com.benmedcalf.alltrailshomework.model.local.PlaceDao
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.SearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository
@Inject
constructor(private val placeDao: PlaceDao) {
    private val _searchResponse =
        MutableStateFlow<PlacesRepositoryResult>(PlacesRepositoryResult.Loading())
    val searchResponseFlow: Flow<PlacesRepositoryResult> = _searchResponse

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val favoritePlaces: Flow<List<PlaceEntity>> = placeDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(placeEntity: PlaceEntity) {
        placeDao.insertPlace(placeEntity)
    }

    suspend fun loadSearchResultsFor(searchParameters: SearchParameters) {
        val response = GooglePlacesService.instance.searchPlaces(
            searchParameters.radius,
            searchParameters.latLng,
            searchParameters.type,
            //TODO("make this properly nullable")
            searchParameters.name?: ""
        )
        if (response.isSuccessful) {
            _searchResponse.value = PlacesRepositoryResult.Success(response.body()!!)
        } else {
            _searchResponse.value = PlacesRepositoryResult.Failure(response.errorBody().toString())
        }
    }

    data class SearchParameters(
        val radius: Int = 50000,
        val latLng: String = "-34.0,151.0",
        val type: String = "restaurant",
        val name: String? = null
    )

    // TODO("should this params be nullable?")
    sealed class PlacesRepositoryResult(
        var value: SearchResponse? = null,
        var error: String? = null
    ) {
        class Success(searchResponseBody: SearchResponse) :
            PlacesRepositoryResult(value = searchResponseBody)

        class Failure(searchResponseError: String) :
            PlacesRepositoryResult(error = searchResponseError)

        class Loading : PlacesRepositoryResult()
    }
}
