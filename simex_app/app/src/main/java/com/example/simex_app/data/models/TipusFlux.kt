package com.example.simex_app.data.models

data class TipusFlux(
    val id: Int,
    val tipus: String,

    // Relación inversa con las Ofertas (ICollection en C#)
    // Usamos una lista vacía por defecto para evitar errores de nulabilidad
    val ofertes: List<Oferte> = emptyList()
)