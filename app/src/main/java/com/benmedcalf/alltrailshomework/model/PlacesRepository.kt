package com.benmedcalf.alltrailshomework.model

import androidx.annotation.WorkerThread
import com.benmedcalf.alltrailshomework.model.local.PlaceDao
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.SearchResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository
@Inject
constructor(private val placeDao: PlaceDao) {
    private val searchResponse: SearchResponse? = null
    val searchResponseFlow: Flow<SearchResponse> = flow {
        emit(loadSearchResultsFor(50000, "-34.0, 151.0"))
    }

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

    suspend fun loadSearchResultsFor(
        radius: Int,
        latLng: String,
        type: String = "restaurant"
    ): SearchResponse {
        //TODO("handle error logic etc")
        return GooglePlacesService.instance.searchPlaces(radius, latLng, type).body()!!
    }
}