package com.example.simex_app.data.models

data class Notificacione(
    val idNotificacion: Int,
    val titulo: String?,
    val mensaje: String?,
    val fecha: String?, // DateOnly? se mapea mejor como String? en Retrofit
    val idUsersend: Int?,
    val idUserrecieve: Int?,

    // Relaciones de navegación (Virtual en C#)
    val idUserrecieveNavigation: Usuari? = null,
    val idUsersendNavigation: Usuari? = null
)