package com.example.simex_app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackingStep(
    val id: Int,
    val ordre: Int,
    val nom: String?
) : Parcelable