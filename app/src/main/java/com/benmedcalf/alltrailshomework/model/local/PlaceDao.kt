package com.benmedcalf.alltrailshomework.model.local

import androidx.room.*


@Dao
interface PlaceDao {
    @Query("Select _id from PlaceEntity")
    fun getFavorites(): List<PlaceEntity>

    @Insert
    fun insert(placeEntity: PlaceEntity)

    @Delete
    suspend fun delete(placeEntity: PlaceEntity)
}