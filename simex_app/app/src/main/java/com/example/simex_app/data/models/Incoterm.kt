package com.example.simex_app.data.models

data class Incoterm(
    val id: Int,
    val tipusIncontermId: Int,
    val trackingStepsId: Int,

    // Relación con las Ofertas (Uno a Muchos)
    val ofertes: List<Oferte> = emptyList(),

    // Objetos de navegación (Relaciones Many-to-One)
    // Usamos el signo ? para evitar errores si la API no los incluye (Lazy Loading)
    val tipusInconterm: TipusIncoterm? = null,
    val trackingSteps: TrackingStep? = null
)