package com.benmedcalf.alltrailshomework.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PlaceDao {
    @Query("Select _id from PlaceEntity")
    fun getFavorites(): List<PlaceEntity>

    @Insert
    fun insert(placeEntity: PlaceEntity)

    @Delete
    suspend fun delete(placeEntity: PlaceEntity)
}