package com.example.simex_app.data.models

data class Port(
    val id: Int,
    val nom: String,
    val ciutatId: Int,

    val ciutat: Ciutat? = null,


    val ofertePortDestis: List<Oferte> = emptyList(),
    val ofertePortOrigens: List<Oferte> = emptyList()
)