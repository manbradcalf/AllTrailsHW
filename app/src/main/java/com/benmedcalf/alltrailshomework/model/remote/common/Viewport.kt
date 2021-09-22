package com.benmedcalf.alltrailshomework.model.remote.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@kotlinx.parcelize.Parcelize
data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
) : Parcelable