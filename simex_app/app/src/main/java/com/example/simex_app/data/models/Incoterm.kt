package com.example.simex_app.data.models

data class Incoterm(
    val id: Int,
    val tipusIncontermId: Int,
    val trackingStepsId: Int,

    val ofertes: List<Oferte> = emptyList(),

    val tipusInconterm: TipusIncoterm? = null,
    val trackingSteps: TrackingStep? = null
)