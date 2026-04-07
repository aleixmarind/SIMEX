package com.example.simex_app.data.models

data class TrackingStep(
    val id: Int,
    val ordre: Int?,
    val nom: String?,

    // Relación con los Incoterms (ICollection en C#)
    // Inicializamos con lista vacía por seguridad
    val incoterms: List<Incoterm> = emptyList()
)