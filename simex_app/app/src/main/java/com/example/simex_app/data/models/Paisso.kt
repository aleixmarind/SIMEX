package com.example.simex_app.data.models

data class Paisso(
    val id: Int,
    val nom: String?,

    // Relación con las Ciudades (Uno a Muchos)
    // Inicializamos con lista vacía para evitar errores de nulabilidad
    val ciutats: List<Ciutat> = emptyList()
)