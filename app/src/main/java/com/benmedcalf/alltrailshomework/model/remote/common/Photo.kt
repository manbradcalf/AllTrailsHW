package com.benmedcalf.alltrailshomework.model.remote.common

data class Photo(
    val height: Int,
    val htmlAttributions: List<String>,
    val photoReference: String,
    val width: Int
)