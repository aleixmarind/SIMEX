package com.example.simex_app.data.models

data class Documento(
    val id: String,
    val nombre: String,
    val tipo: TipoDocumento,
    val url: String,
    val fechaSubida: Long,
    val validado: Boolean
)

enum class TipoDocumento {
    FACTURA,
    CERTIFICADO_ORIGEN,
    DOCUMENTO_ADUANERO,
    CONTRATO,
    OTRO
}