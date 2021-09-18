package com.benmedcalf.alltrailshomework.model.remote.findPlaceByText


import com.benmedcalf.alltrailshomework.model.remote.common.Geometry
import com.benmedcalf.alltrailshomework.model.remote.common.OpeningHours
import com.google.gson.annotations.SerializedName

data class Candidate(
    val formattedAddress: String,
    val geometry: Geometry,
    val name: String,
    val openingHours: OpeningHours,
    val rating: Double
)