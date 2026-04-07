package com.example.simex_app.data.models

data class Transportiste(
    val id: Int,
    val nom: String,
    val ciutatId: Int,

    // Relación con la Ciudad (Muchos a Uno)
    // Usamos ? = null por si la API hace Lazy Loading y no incluye el objeto
    val ciutat: Ciutat? = null,

    // Colección de Ofertas asignadas a este transportista (Uno a Muchos)
    val ofertes: List<Oferte> = emptyList()
)