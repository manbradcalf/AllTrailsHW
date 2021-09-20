package com.benmedcalf.alltrailshomework.model

import com.benmedcalf.alltrailshomework.model.remote.common.Geometry
import com.benmedcalf.alltrailshomework.model.remote.common.Photo

data class Restaurant(
    val isFavorite: Boolean,
    val geometry: Geometry,
    val name: String,
    val photos: List<Photo>?,
    val placeId: String,
    val priceLevel: Int,
    val rating: Double,
    val userRatingsTotal: Int,
) {
    fun formatPrice(priceLevel: Int): String {
        var priceString = ""
        repeat(priceLevel) {
            priceString += "$"
        }
        return priceString
    }
}