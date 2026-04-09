package com.example.simex_app.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("rolId") val rolId: Int,
    @SerializedName("tipo") val tipo: String,           // "Agente" o "Cliente"
    @SerializedName("nombreRolReal") val nombreRolReal: String? = null
)
