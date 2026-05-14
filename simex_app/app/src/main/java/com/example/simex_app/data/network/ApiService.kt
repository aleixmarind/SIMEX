package com.example.simex_app.data.network

import com.example.simex_app.data.models.Comanda
import com.example.simex_app.data.models.DecisionOfertaDTO
import com.example.simex_app.data.models.LoginResponse
import com.example.simex_app.data.models.Usuari
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    // --- SERVIDOR EXTERNO (DNI) ---
    @Multipart
    @POST("api/dni/upload/{id}")
    suspend fun uploadDniEncrypted(
        @Path("id") clienteId: Int,
        @Part file: MultipartBody.Part
    ): Response<String>

}