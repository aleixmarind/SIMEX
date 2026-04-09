package com.example.simex_app.data.models

data class Comanda(
    val id: Int,
    val numPedido: String,
    val nombreOferta: String,
    val puertoOrigen: String,
    val puertoDestino: String,
    val estado: String,
    val fechaEntrega: String
)