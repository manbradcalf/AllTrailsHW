package com.benmedcalf.alltrailshomework.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {
    @Query("Select * from PlaceEntity")
    fun getAll(): Flow<List<PlaceEntity>>

    @Insert
    fun insertPlace(placeEntity: PlaceEntity)
}