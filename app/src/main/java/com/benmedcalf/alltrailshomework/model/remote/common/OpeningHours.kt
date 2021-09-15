package com.benmedcalf.alltrailshomework.model.remote.common


import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean
)