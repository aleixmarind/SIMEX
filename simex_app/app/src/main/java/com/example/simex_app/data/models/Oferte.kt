package com.example.simex_app.data.models

data class Oferte(
    val id: Int = 0,
    val tipusTransportId: Int,
    val tipusFluxeId: Int,
    val tipusCarregaId: Int,
    val incotermId: Int,
    val clientId: Int,
    val comentaris: String?,
    val agentComercialId: Int?,
    val transportistaId: Int?,
    val pesBrut: Double?,  // decimal en C# -> Double en Kotlin
    val volum: Double?,
    val tipusValidacioId: Int,
    val portOrigenId: Int?,
    val portDestiId: Int?,
    val aeroportOrigenId: Int?,
    val aeroportDestiId: Int?,
    val liniaTransportMaritimId: Int?,
    val estatOfertaId: Int,
    val operadorId: Int,
    val dataCreacio: String, // DateOnly se recibe mejor como String
    val dataValidessaInicial: String?,
    val dataValidessaFina: String?,
    val raoRebuig: String?,
    val tipusContenidorId: Int?,
    val nombreOferta: String?,
    val fechaEnvio: String?,
    val fechaEntrega: String?,
    val numPedido: Int?,
    val idFreight: Int?,
    val active: Int,

    // --- Relaciones virtuales (Navegación) ---
    val aeroportDesti: Aeroport? = null,
    val aeroportOrigen: Aeroport? = null,
    val documentacions: List<Documentacion> = emptyList(),
    val estatOferta: EstatsOferte? = null,
    val incoterm: Any? = null, // Sustituir Any por la clase real cuando la tengas
    val liniaTransportMaritim: Any? = null,
    val operador: Usuari? = null,
    val portDesti: Any? = null,
    val portOrigen: Any? = null,
    val tipusCarrega: Any? = null,
    val tipusContenidor: Any? = null,
    val tipusFluxe: Any? = null,
    val tipusTransport: Any? = null,
    val tipusValidacio: Any? = null,
    val transportista: Any? = null
)