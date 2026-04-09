package com.example.simex_app.ui.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.simex_app.R

// Es VITAL añadir ": AppCompatActivity()" para que el Manifest lo reconozca
class HomePageClienteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Si ya tienes un layout creado, descomenta la línea de abajo:
        // setContentView(R.layout.activity_home_cliente)
    }
}