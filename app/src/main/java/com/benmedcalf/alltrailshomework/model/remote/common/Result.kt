package com.benmedcalf.alltrailshomework.model.remote.common

data class Result(
    val businessStatus: String,
    val geometry: Geometry,
    val icon: String,
    val iconBackgroundColor: String,
    val iconMaskBaseUri: String,
    val name: String,
    val openingHours: OpeningHours,
    val permanentlyClosed: Boolean,
    val photos: List<Photo>,
    val placeId: String,
    val plusCode: PlusCode,
    val priceLevel: Int,
    val rating: Double,
    val reference: String,
    val scope: String,
    val types: List<String>,
    val userRatingsTotal: Int,
    val vicinity: String
)