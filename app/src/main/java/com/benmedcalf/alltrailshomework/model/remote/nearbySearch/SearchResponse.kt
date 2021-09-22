package com.benmedcalf.alltrailshomework.model.remote.nearbySearch


import com.benmedcalf.alltrailshomework.model.remote.common.PlaceDetails

data class SearchResponse(
    val htmlAttributions: List<Any>,
    val results: List<PlaceDetails>,
    val status: String
)