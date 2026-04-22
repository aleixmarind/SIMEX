package com.example.simex_app.data.network

import com.example.simex_app.data.models.Comanda
import com.example.simex_app.data.models.DecisionOfertaDTO
import com.example.simex_app.data.models.LoginResponse
import com.example.simex_app.data.models.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/Usuaris/login")
    suspend fun login(@Body datos: Map<String, String>): Response<LoginResponse>

    @GET("api/Usuaris/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Usuari

    // --- CLIENTE ---
    @GET("api/Ofertes/Comandas/{id}")
    suspend fun getComandas(@Path("id") clienteId: Int): List<Comanda>

    @GET("api/Ofertes/Pendientes/{id}")
    suspend fun getOfertasPendientes(@Path("id") clienteId: Int): List<Comanda>

    @POST("api/Ofertes/{id}/decidir")
    suspend fun decidirOferta(@Path("id") id: Int, @Body decision: DecisionOfertaDTO): Response<Unit>

    // --- AGENTE ---
    @GET("api/Ofertes/Agente/Stats")
    suspend fun getAgenteStats(): Map<String, Int>

    @GET("api/Ofertes/Agente/Recientes")
    suspend fun getComandasGlobales(): List<Comanda>

    @POST("api/Ofertes/{id}/tracking")
    suspend fun actualizarTracking(@Path("id") id: Int, @Body nuevoTrackingId: Int): Response<Unit>

    // --- DNI UPLOAD ---
    @POST("api/Usuaris/{id}/dni-frontal")
    suspend fun subirDniFrontal(@Path("id") id: Int, @Body base64: String): Response<Unit>

    @POST("api/Usuaris/{id}/dni-trasero")
    suspend fun subirDniTrasero(@Path("id") id: Int, @Body base64: String): Response<Unit>

}