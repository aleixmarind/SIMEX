package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simex_app.R
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.databinding.ActivityComandaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComandasAgenteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComandaBinding
    private lateinit var adapter: ComandaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val agenteId = intent.getIntExtra("CLIENTE_ID", -1)
        val nombreAgente = intent.getStringExtra("USER_NAME") ?: "Agente"

        setupRecyclerView()
        setupBottomNavigation(agenteId, nombreAgente)
        obtenerComandasGlobales()
    }

    private fun setupRecyclerView() {
        binding.rvComandas.layoutManager = LinearLayoutManager(this)
        adapter = ComandaAdapter(emptyList()) { comanda ->
            val intent = Intent(this, DetalleComandaAgenteActivity::class.java)
            intent.putExtra("DETALLE_COMANDA", comanda)
            startActivity(intent)
        }
        binding.rvComandas.adapter = adapter
    }

    private fun setupBottomNavigation(agenteId: Int, nombreAgente: String) {
        binding.bottomNavigation.selectedItemId = R.id.nav_comandas
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomePageAgenteActivity::class.java)
                    intent.putExtra("CLIENTE_ID", agenteId)
                    intent.putExtra("USER_NAME", nombreAgente)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_comandas -> true
                R.id.nav_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("CLIENTE_ID", agenteId)
                    intent.putExtra("USER_NAME", nombreAgente)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtenerComandasGlobales() {
        lifecycleScope.launch {
            try {
                val listaComandas = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getComandasGlobales()
                }

                if (listaComandas.isNotEmpty()) {
                    adapter.updateList(listaComandas)
                } else {
                    Toast.makeText(this@ComandasAgenteActivity, "No hay comandas disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener comandas", e)
                Toast.makeText(this@ComandasAgenteActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        obtenerComandasGlobales()
    }
}
