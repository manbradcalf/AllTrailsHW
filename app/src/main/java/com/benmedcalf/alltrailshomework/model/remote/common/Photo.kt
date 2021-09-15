package com.benmedcalf.alltrailshomework.model.remote.common


import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("height")
    val height: Int,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>,
    @SerializedName("photo_reference")
    val photoReference: String,
    @SerializedName("width")
    val width: Int
)