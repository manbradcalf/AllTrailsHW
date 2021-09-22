package com.benmedcalf.alltrailshomework.model.remote.common

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Southwest(
    val lat: Double,
    val lng: Double
) : Parcelable