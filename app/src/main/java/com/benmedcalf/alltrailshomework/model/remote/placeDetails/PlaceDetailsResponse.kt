package com.benmedcalf.alltrailshomework.model.remote.placeDetails


import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.gson.annotations.SerializedName

data class PlaceDetailsResponse(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>,
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: String
)