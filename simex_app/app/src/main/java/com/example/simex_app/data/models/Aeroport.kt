package com.example.simex_app.data.models

data class Aeroport(
    val id: Int,
    val codi: String,
    val nom: String?,
    val ciutatId: Int,
    val ciutat: Ciutat? = null,
    val oferteAeroportDestis: List<Oferte> = emptyList(),
    val oferteAeroportOrigens: List<Oferte> = emptyList()
)