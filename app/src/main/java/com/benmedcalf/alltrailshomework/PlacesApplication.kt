package com.benmedcalf.alltrailshomework

import android.app.Application
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.local.PlaceDatabase

class PlacesApplication : Application() {
    val database by lazy { PlaceDatabase.getDatabase(this) }
    val repository by lazy { PlacesRepository(database.placeDao()) }
}