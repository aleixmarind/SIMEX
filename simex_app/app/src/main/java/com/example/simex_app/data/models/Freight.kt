package com.example.simex_app.data.models

data class Freight(
    val idFreight: Int,
    val tipus: String?,

    // Relación con las Ofertas (Uno a Muchos)
    // Usamos emptyList() para que la App no se rompa si no hay ofertas asociadas
    val ofertes: List<Oferte> = emptyList()
)