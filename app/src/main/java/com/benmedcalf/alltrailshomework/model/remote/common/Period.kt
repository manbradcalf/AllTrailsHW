package com.benmedcalf.alltrailshomework.model.remote.common


import com.google.gson.annotations.SerializedName

data class Period(
    @SerializedName("close")
    val close: Close,
    @SerializedName("open")
    val `open`: Open
)