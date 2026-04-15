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

class HomePageAgenteActivity : AppCompatActivity() {

    private lateinit var adapter: ComandaAdapter
    private lateinit var rvRecentOrders: RecyclerView
    private lateinit var cardNoOrders: MaterialCardView
    private lateinit var tvStatsActivas: TextView
    private lateinit var tvStatsOfertas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepageagente)

        val agenteId = intent.getIntExtra("CLIENTE_ID", -1)
        val nombreAgente = intent.getStringExtra("USER_NAME") ?: "Agente"

        val tvWelcomeName = findViewById<TextView>(R.id.tvWelcomeName)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        rvRecentOrders = findViewById(R.id.rvRecentOrdersGlobal)
        cardNoOrders = findViewById(R.id.cardNoOrdersGlobal)
        tvStatsActivas = findViewById(R.id.tvStatsActivas)
        tvStatsOfertas = findViewById(R.id.tvStatsOfertas)

        tvWelcomeName.text = "Hola, $nombreAgente"

        setupRecyclerView()
        obtenerDatosAgente()

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_comandas -> {
                    val intentComandas = Intent(this, ComandasAgenteActivity::class.java)
                    intentComandas.putExtra("CLIENTE_ID", agenteId)
                    intentComandas.putExtra("USER_NAME", nombreAgente)
                    startActivity(intentComandas)
                    true
                }
                R.id.nav_perfil -> {
                    val intentPerfil = Intent(this, PerfilActivity::class.java)
                    intentPerfil.putExtra("CLIENTE_ID", agenteId)
                    intentPerfil.putExtra("USER_NAME", nombreAgente)
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
            val intent = Intent(this, DetalleComandaAgenteActivity::class.java)
            intent.putExtra("DETALLE_COMANDA", comanda)
            startActivity(intent)
        }
        rvRecentOrders.adapter = adapter
    }

    private fun obtenerDatosAgente() {
        lifecycleScope.launch {
            try {
                val stats = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getAgenteStats()
                }
                tvStatsActivas.text = stats["activas"].toString()
                tvStatsOfertas.text = stats["ofertas"].toString()

                val comandas = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getComandasGlobales()
                }

                if (comandas.isNotEmpty()) {
                    adapter.updateList(comandas.take(3))
                    rvRecentOrders.visibility = View.VISIBLE
                    cardNoOrders.visibility = View.GONE
                } else {
                    rvRecentOrders.visibility = View.GONE
                    cardNoOrders.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("AGENTE_HOME_ERROR", "Error: ${e.message}")
                Toast.makeText(this@HomePageAgenteActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
