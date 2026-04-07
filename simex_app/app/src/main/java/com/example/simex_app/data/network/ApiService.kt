package com.example.simex_app.data.network

import com.example.simex_app.data.models.Pais
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("Paissos")
    suspend fun getPaissos(): List<Pais>

    @GET("Paissos/{id}")
    suspend fun getPaisById(@Path("id") id: Int): Pais

}