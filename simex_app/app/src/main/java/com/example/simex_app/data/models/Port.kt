package com.example.simex_app.data.models

data class Port(
    val id: Int,
    val nom: String,
    val ciutatId: Int,

    // Relación con la Ciudad (Muchos a Uno)
    val ciutat: Ciutat? = null,

    // Relaciones inversas con Ofertas (Uno a Muchos)
    // Distinguimos entre puerto de salida y puerto de llegada
    val ofertePortDestis: List<Oferte> = emptyList(),
    val ofertePortOrigens: List<Oferte> = emptyList()
)