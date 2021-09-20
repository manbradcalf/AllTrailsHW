package com.benmedcalf.alltrailshomework.model.remote.placeDetails


import com.benmedcalf.alltrailshomework.model.remote.common.PlaceDetails

data class PlaceDetailsResponse(
    val htmlAttributions: List<Any>,
    val placeDetails: PlaceDetails,
    val status: String
)