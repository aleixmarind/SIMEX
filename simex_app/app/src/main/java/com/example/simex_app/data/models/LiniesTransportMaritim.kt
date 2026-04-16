package com.example.simex_app.data.models

data class LiniesTransportMaritim(
    val id: Int,
    val nom: String,
    val ciutatId: Int,

    val ciutat: Ciutat? = null,

    val ofertes: List<Oferte> = emptyList()
)