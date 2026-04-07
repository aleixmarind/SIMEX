package com.example.simex_app.data.models

data class TipusCarrega(
    val id: Int,
    val tipus: String,

    // Colección de Ofertas que usan este tipo de carga
    // Usamos List y la inicializamos vacía por defecto
    val ofertes: List<Oferte> = emptyList()
)