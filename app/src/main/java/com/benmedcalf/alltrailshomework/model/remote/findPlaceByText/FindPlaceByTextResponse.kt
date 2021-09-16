package com.benmedcalf.alltrailshomework.model.remote.findPlaceByText


import com.google.gson.annotations.SerializedName

data class FindPlaceByTextResponse(
    val candidates: List<Candidate>,
    val status: String
)