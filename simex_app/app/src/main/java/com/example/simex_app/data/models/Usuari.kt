package com.example.simex_app.data.models

data class Usuari(
    val active: Int,
    val cognoms: String,
    val contrasenya: String,
    val correu: String,
    val id: Int,
    val nom: String,
    val notificacioneIdUserrecieveNavigations: List<Any>,
    val notificacioneIdUsersendNavigations: List<Any>,
    val ofertes: List<Any>,
    val rol: Any,
    val rolId: Int
)