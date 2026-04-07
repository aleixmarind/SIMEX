package com.example.simex_app.data.models

data class Rol(
    val id: Int,
    val rol1: String,

    // Relación con los Usuarios que tienen este Rol
    // Inicializamos con lista vacía por seguridad en el mapeo
    val usuaris: List<Usuari> = emptyList()
)