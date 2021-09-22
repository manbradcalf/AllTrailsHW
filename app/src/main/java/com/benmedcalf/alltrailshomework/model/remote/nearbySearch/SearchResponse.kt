package com.benmedcalf.alltrailshomework.model.remote.nearbySearch


import com.benmedcalf.alltrailshomework.model.remote.common.PlaceDetails
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val htmlAttributions: List<Any>,
    val results: List<PlaceDetails>,
    val status: String
)