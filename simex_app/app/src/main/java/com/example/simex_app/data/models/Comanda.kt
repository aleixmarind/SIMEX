package com.example.simex_app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comanda(
    val id: Int,
    val numPedido: String?,
    val nombreOferta: String?,
    val puertoOrigen: String?,
    val puertoDestino: String?,
    val estado: String?,
    val fechaEntrega: String?,
    // --- AÑADE ESTO ---
    val trackingActualId: Int?,
    val pasosSeguimiento: List<TrackingStep>? = emptyList()
) : Parcelable