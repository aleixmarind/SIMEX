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

class ComandasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComandaBinding
    private lateinit var adapter: ComandaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityComandaBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val clienteId = intent.getIntExtra("CLIENTE_ID", 1004)
            val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Cliente"

            setupRecyclerView()
            setupBottomNavigation(clienteId, nombreUsuario)
            obtenerComandasDesdeApi(clienteId)
        } catch (e: Exception) {
            Log.e("ComandasActivity", "Error al inflar layout", e)
            setContentView(R.layout.activity_comanda)
            Toast.makeText(this, "Error crítico al cargar la vista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.rvComandas.layoutManager = LinearLayoutManager(this)
        
        adapter = ComandaAdapter(emptyList()) { comanda ->
            val intent = Intent(this, DetalleComandaActivity::class.java)
            intent.putExtra("DETALLE_COMANDA", comanda)
            startActivity(intent)
        }
        binding.rvComandas.adapter = adapter
    }

    private fun setupBottomNavigation(clienteId: Int, nombreUsuario: String) {
        binding.bottomNavigation.selectedItemId = R.id.nav_comandas
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomePageClienteActivity::class.java)
                    intent.putExtra("CLIENTE_ID", clienteId)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_comandas -> true
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
    }

    private fun obtenerComandasDesdeApi(clienteId: Int) {
        lifecycleScope.launch {
            try {
                val listaComandas = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getComandas(clienteId)
                }

                if (listaComandas.isNotEmpty()) {
                    adapter.updateList(listaComandas)
                } else {
                    Toast.makeText(this@ComandasActivity, "No hay comandas disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener comandas", e)
                Toast.makeText(this@ComandasActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}