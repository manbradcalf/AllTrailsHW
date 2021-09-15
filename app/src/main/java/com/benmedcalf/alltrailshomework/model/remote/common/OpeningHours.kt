package com.benmedcalf.alltrailshomework.model.remote.common


import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean,
    @SerializedName("periods")
    val periods: List<Period>,
    @SerializedName("weekday_text")
    val weekdayText: List<String>
)