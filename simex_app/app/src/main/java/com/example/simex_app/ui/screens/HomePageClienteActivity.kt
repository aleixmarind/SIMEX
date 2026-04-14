package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.R
import com.example.simex_app.data.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageClienteActivity : AppCompatActivity() {

    private lateinit var adapter: ComandaAdapter
    private lateinit var rvRecentOrders: RecyclerView
    private lateinit var cardNoOrders: MaterialCardView
    private lateinit var btnVerOfertas: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepagecliente)

        // Recibimos el ID del Login
        val clienteId = intent.getIntExtra("CLIENTE_ID", 1004)
        val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Cliente"

        // Referencias de la UI
        val tvWelcomeName = findViewById<TextView>(R.id.tvWelcomeName)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        rvRecentOrders = findViewById(R.id.rvRecentOrders)
        cardNoOrders = findViewById(R.id.cardNoOrders)
        btnVerOfertas = findViewById(R.id.btnVerOfertas)

        tvWelcomeName.text = "Hola, $nombreUsuario"

        setupRecyclerView()
        obtenerComandasRecientes(clienteId)

        // Botón Ver Ofertas
        btnVerOfertas.setOnClickListener {
            val intentOfertas = Intent(this, OfertasActivity::class.java)
            intentOfertas.putExtra("CLIENTE_ID", clienteId)
            intentOfertas.putExtra("USER_NAME", nombreUsuario)
            startActivity(intentOfertas)
        }

        // Configuración de la barra de navegación
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_comandas -> {
                    val intentComandas = Intent(this, ComandasActivity::class.java)
                    intentComandas.putExtra("CLIENTE_ID", clienteId)
                    intentComandas.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intentComandas)
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
                    val intentPerfil = Intent(this, PerfilActivity::class.java)
                    intentPerfil.putExtra("CLIENTE_ID", clienteId)
                    intentPerfil.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intentPerfil)
                    true
                }
                else -> false
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun setupRecyclerView() {
        rvRecentOrders.layoutManager = LinearLayoutManager(this)
        adapter = ComandaAdapter(emptyList()) { comanda ->
            val intent = Intent(this, DetalleComandaActivity::class.java)
            intent.putExtra("DETALLE_COMANDA", comanda)
            startActivity(intent)
        }
        rvRecentOrders.adapter = adapter
    }

    private fun obtenerComandasRecientes(clienteId: Int) {
        lifecycleScope.launch {
            try {
                val listaComandas = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getComandas(clienteId)
                }

                if (listaComandas.isNotEmpty()) {
                    // Mostramos las 2 más recientes
                    val recientes = listaComandas.take(2)
                    adapter.updateList(recientes)
                    rvRecentOrders.visibility = View.VISIBLE
                    cardNoOrders.visibility = View.GONE
                } else {
                    rvRecentOrders.visibility = View.GONE
                    cardNoOrders.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("HOME_API_ERROR", "Error al obtener comandas: ${e.message}", e)
                rvRecentOrders.visibility = View.GONE
                cardNoOrders.visibility = View.VISIBLE
            }
        }
    }
}