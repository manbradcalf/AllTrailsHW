package com.benmedcalf.alltrailshomework.model.remote.common

import com.google.gson.annotations.SerializedName

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
    @SerializedName("place_id")
    val placeId: String,
    val plusCode: PlusCode,
    val priceLevel: Int,
    val rating: Double,
    val reference: String,
    val scope: String,
    val types: List<String>,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int,
    val vicinity: String
)