package com.example.simex_app.data.models

data class Oferta(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val moneda: String,
    val fechaCreacion: Long,
    val estado: EstadoOferta,
    val proveedorId: String,
    val clienteId: String
)

enum class EstadoOferta {
    PENDIENTE,
    ACEPTADA,
    DENEGADA
}