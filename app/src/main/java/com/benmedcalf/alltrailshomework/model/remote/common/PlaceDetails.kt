package com.benmedcalf.alltrailshomework.model.remote.common

import com.benmedcalf.alltrailshomework.model.Restaurant

data class PlaceDetails(
    val businessStatus: String,
    val geometry: Geometry,
    val icon: String,
    val iconBackgroundColor: String,
    val iconMaskBaseUri: String,
    val name: String,
    val openingHours: OpeningHours,
    val permanentlyClosed: Boolean,
    val photos: List<Photo>?,
    val placeId: String,
    val plusCode: PlusCode,
    val priceLevel: Int,
    val rating: Double,
    val reference: String,
    val scope: String,
    val types: List<String>,
    val userRatingsTotal: Int,
    val vicinity: String,
) {
    fun toRestaurant() = Restaurant(
        //default value for is favorite
        isFavorite = false,
        name = this.name,
        placeId = this.placeId,
        geometry = this.geometry,
        userRatingsTotal = this.userRatingsTotal,
        rating = this.rating,
        priceLevel = this.priceLevel,
        photoReference = this.photos?.first()?.photoReference
    )
}