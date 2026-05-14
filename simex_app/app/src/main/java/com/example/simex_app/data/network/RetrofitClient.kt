package com.example.simex_app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.simex_app.data.network.ApiService

object RetrofitClient {
    // URL de tu API principal (.NET)
    private const val BASE_URL = "http://10.0.2.2:5274/"
    
    // URL de tu nuevo servidor en IntelliJ (Spring Boot)
    // Cambiado a 8081 para evitar el conflicto de puertos
    private const val DNI_SERVER_URL = "http://10.0.2.2:8081/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Instancia específica para el servidor de DNI
    val dniInstance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DNI_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
