package com.benmedcalf.alltrailshomework.model.remote.nearbySearch


import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val htmlAttributions: List<Any>,
    val results: List<Result>,
    val status: String
)