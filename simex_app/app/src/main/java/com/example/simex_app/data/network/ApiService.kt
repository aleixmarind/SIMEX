package com.example.simex_app.data.network

import com.example.simex_app.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Enviamos un Mapa de strings y recibimos el objeto LoginResponse si es correcto
    @POST("api/Usuaris/login")
    suspend fun login(@Body datos: Map<String, String>): Response<LoginResponse>

}