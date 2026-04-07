package com.example.simex_app.data.models

data class Documentacion(
    val idDocumento: Int,
    val nombreDoc: String,
    val urlArchivo: String,
    val idOferta: Int,

    // Relación con la Oferta (Navegación)
    // Se pone como opcional (?) para que no explote si el JSON no la incluye
    val idOfertaNavigation: Oferte? = null
)