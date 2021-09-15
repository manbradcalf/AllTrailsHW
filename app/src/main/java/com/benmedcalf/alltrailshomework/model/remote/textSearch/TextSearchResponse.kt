package com.benmedcalf.alltrailshomework.model.remote.textSearch


import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.gson.annotations.SerializedName

data class TextSearchResponse(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("status")
    val status: String
)