package com.benmedcalf.alltrailshomework.model.remote.common

data class OpeningHours(
    val openNow: Boolean,
    val periods: List<Period>,
    val weekdayText: List<String>
)