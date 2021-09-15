package com.benmedcalf.alltrailshomework.model.remote.findPlaceByText


import com.google.gson.annotations.SerializedName

data class FindPlaceByTextResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>,
    @SerializedName("status")
    val status: String
)