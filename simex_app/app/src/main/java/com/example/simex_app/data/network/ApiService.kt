package com.example.simex_app.data.network

import com.example.simex_app.data.models.Pais
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // 1. Obtener todos los países (y sus ciudades)
    @GET("Paissos")
    suspend fun getPaissos(): List<Pais>

    // 2. Obtener un solo país por su ID (Opcional)
    @GET("Paissos/{id}")
    suspend fun getPaisById(@Path("id") id: Int): Pais

}