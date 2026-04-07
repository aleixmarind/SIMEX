package com.example.simex_app.data.models

data class EstatsOferte(
    val id: Int,
    val estat: String,

    // Colección de Ofertas relacionadas
    // Usamos List y la inicializamos vacía por seguridad
    val ofertes: List<Oferte> = emptyList()
)