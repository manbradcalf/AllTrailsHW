package com.benmedcalf.alltrailshomework.model.remote.findPlaceByText


import com.benmedcalf.alltrailshomework.model.remote.common.Geometry
import com.benmedcalf.alltrailshomework.model.remote.common.OpeningHours
import com.google.gson.annotations.SerializedName

data class Candidate(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("name")
    val name: String,
    @SerializedName("opening_hours")
    val openingHours: OpeningHours,
    @SerializedName("rating")
    val rating: Double
)