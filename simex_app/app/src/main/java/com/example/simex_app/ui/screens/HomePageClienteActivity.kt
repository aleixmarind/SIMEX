package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simex_app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageClienteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepagecliente)

        // Referencias de la UI
        val tvWelcomeName = findViewById<TextView>(R.id.tvWelcomeName)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Aquí podrías recibir el nombre del usuario desde el Intent
        val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Cliente"
        tvWelcomeName.text = "Hola, $nombreUsuario"

        // Configuración de la barra de navegación
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estás aquí
                    true
                }
                R.id.nav_comandas -> {
                    val intent = Intent(this, ComandasActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_documentos -> {
                    Toast.makeText(this, "Ir a Documentos", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_juego -> {
                    Toast.makeText(this, "Ir al Juego", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Ir al Perfil", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Seleccionar Home por defecto
        bottomNavigation.selectedItemId = R.id.nav_home
    }
}