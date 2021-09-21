package com.benmedcalf.alltrailshomework.model

import androidx.annotation.WorkerThread
import com.benmedcalf.alltrailshomework.model.local.PlaceDao
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository
@Inject
constructor(private val placeDao: PlaceDao) {
    lateinit var userLocation: LatLng
    private val _searchResponse =
        MutableStateFlow<Result>(Result.Loading())
    val searchResponseFlow: Flow<Result> = _searchResponse

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

    suspend fun loadSearchResultsFor(query: String) {
        val response = GooglePlacesService.instance.searchPlaces(
            50000,
            "${userLocation.latitude},${userLocation.longitude}",
            "restaurant",
            query
        )
        if (response.isSuccessful) {
            val restaurants = mutableListOf<Restaurant>()
            response.body()?.results?.forEach { result ->
                restaurants.add(result.toRestaurant())
            }
            _searchResponse.value = Result.Success(restaurants)
        } else {
            _searchResponse.value = Result.Failure(response.errorBody().toString())
        }
    }

    class SearchParameters(
        var radius: Int? = null,
        var latLng: String,
        var type: String? = null,
        var name: String? = null
    )

    sealed class Result(
        var value: List<Restaurant>? = null,
        var error: String? = null
    ) {
        class Success(restaurants: List<Restaurant>) :
            Result(value = restaurants)

        class Failure(searchResponseError: String) :
            Result(error = searchResponseError)

        class Loading : Result()
    }
}
