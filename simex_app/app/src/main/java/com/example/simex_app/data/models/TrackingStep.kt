package com.example.simex_app.data.models

data class TrackingStep(
    val id: String,
    val descripcion: String,
    val ubicacion: String,
    val fecha: Long,
    val estado: EstadoTracking
)

enum class EstadoTracking {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADO,
    RETRASADO
}