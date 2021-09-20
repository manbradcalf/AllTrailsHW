package com.benmedcalf.alltrailshomework.model.remote.nearbySearch


import com.benmedcalf.alltrailshomework.model.remote.common.PlaceDetails
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val htmlAttributions: List<Any>,
    @SerializedName("results")
    val placeDetails: List<PlaceDetails>,
    val status: String
)