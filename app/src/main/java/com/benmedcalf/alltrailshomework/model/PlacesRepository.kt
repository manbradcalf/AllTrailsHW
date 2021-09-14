package com.benmedcalf.alltrailshomework.model

import androidx.annotation.WorkerThread
import com.benmedcalf.alltrailshomework.model.local.PlaceDao
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import kotlinx.coroutines.flow.Flow

class PlacesRepository(private val placeDao: PlaceDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allPlaces: Flow<List<PlaceEntity>> = placeDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(placeEntity: PlaceEntity) {
        placeDao.insertPlace(placeEntity)
    }
}