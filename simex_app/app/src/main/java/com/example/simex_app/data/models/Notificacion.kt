package com.example.simex_app.data.models

data class Notificacion(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val fecha: Long,
    val leida: Boolean = false,
    val tipo: TipoNotificacion
)

enum class TipoNotificacion {
    OFERTA,
    TRACKING,
    DOCUMENTACION,
    GENERAL
}