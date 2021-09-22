package com.benmedcalf.alltrailshomework.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceEntity(
    @PrimaryKey val _id: String,
    @ColumnInfo(name = "place_name") val placeName: String?
)