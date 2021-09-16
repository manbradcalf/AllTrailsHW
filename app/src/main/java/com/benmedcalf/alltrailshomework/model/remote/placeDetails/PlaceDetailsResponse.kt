package com.benmedcalf.alltrailshomework.model.remote.placeDetails


import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.gson.annotations.SerializedName

data class PlaceDetailsResponse(
    val htmlAttributions: List<Any>,
    val result: Result,
    val status: String
)