package com.example.simex_app.data.models

data class TipusIncoterm(
    val id: Int,
    val codi: String?,
    val nom: String?,

    // Relación con los Incoterms específicos
    // Inicializamos con lista vacía por seguridad en el mapeo JSON
    val incoterms: List<Incoterm> = emptyList()
)