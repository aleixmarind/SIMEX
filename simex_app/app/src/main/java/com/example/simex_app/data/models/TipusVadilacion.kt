package com.example.simex_app.data.models

data class TipusValidacion(
    val id: Int,
    val tipus: String,

    // Relación con las Ofertas que requieren este tipo de validación
    // Inicializamos con lista vacía por seguridad en el mapeo JSON
    val ofertes: List<Oferte> = emptyList()
)