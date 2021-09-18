package com.benmedcalf.alltrailshomework.model.remote.textSearch


import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.google.gson.annotations.SerializedName

data class TextSearchResponse(
    val htmlAttributions: List<Any>,
    val results: List<Result>,
    val status: String
)