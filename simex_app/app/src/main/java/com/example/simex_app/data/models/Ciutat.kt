package com.example.simex_app.data.models

data class Ciutat(
    val id: Int,
    val nom: String?,
    val paisId: Int,

    // Relación con el padre (País)
    // Usamos el nombre que tengas en C# (Paisso o Pais)
    val pais: Paisso? = null,

    // Listas de relaciones (Colecciones)
    // Inicializamos con lista vacía para evitar NullPointerExceptions
    val aeroports: List<Aeroport> = emptyList(),
    val liniesTransportMaritims: List<Any> = emptyList(), // Cambiar Any por el modelo real cuando lo tengas
    val ports: List<Any> = emptyList(),
    val transportistes: List<Any> = emptyList()
)