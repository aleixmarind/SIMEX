package com.example.simex_app.data.models

data class TipusContenidor(
    val id: Int,
    val tipus: String,

    // Relación inversa con las Ofertas que usan este contenedor
    // Inicializamos con lista vacía por seguridad
    val ofertes: List<Oferte> = emptyList()
)