package com.benmedcalf.alltrailshomework.model.remote.common


import com.google.gson.annotations.SerializedName

data class Close(
    @SerializedName("day")
    val day: Int,
    @SerializedName("time")
    val time: String
)