package com.example.simex_app.data.models

data class LiniesTransportMaritim(
    val id: Int,
    val nom: String,
    val ciutatId: Int,

    // Relación con la Ciudad (Muchos a Uno)
    // Usamos ? = null por si la API no carga el objeto relacionado
    val ciutat: Ciutat? = null,

    // Relación con las Ofertas (Uno a Muchos)
    // Inicializamos con emptyList() para evitar NullPointerException
    val ofertes: List<Oferte> = emptyList()
)