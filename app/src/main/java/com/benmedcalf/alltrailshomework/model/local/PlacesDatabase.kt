package com.benmedcalf.alltrailshomework.model.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlaceEntity::class], version = 1, exportSchema = false)
abstract class PlacesDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}


