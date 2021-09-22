package com.benmedcalf.alltrailshomework.model

import android.os.Parcelable
import com.benmedcalf.alltrailshomework.model.remote.common.Geometry

@kotlinx.parcelize.Parcelize
data class Restaurant(
    var isFavorite: Boolean,
    val geometry: Geometry,
    val name: String,
    val photoReference: String?,
    val placeId: String,
    val priceLevel: Int,
    val rating: Double,
    val userRatingsTotal: Int,
) : Parcelable {
    companion object {
        fun formatPrice(priceLevel: Int): String {
            var priceString = ""
            repeat(priceLevel) {
                priceString += "$"
            }
            return priceString
        }
    }
}