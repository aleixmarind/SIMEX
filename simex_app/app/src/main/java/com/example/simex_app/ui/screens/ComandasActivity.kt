package com.example.simex_app.ui.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

            setupRecyclerView()
            obtenerComandasDesdeApi()
        } catch (e: Exception) {
            Log.e("ComandasActivity", "Error al inflar layout", e)
            setContentView(com.example.simex_app.R.layout.activity_comanda)
            Toast.makeText(this, "Error crítico al cargar la vista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        // Usamos el ID directamente si binding falla o para mayor seguridad
        val rv = binding.rvComandas 
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun obtenerComandasDesdeApi() {
        lifecycleScope.launch {
            try {
                // ID 1004 que es Jose Garcia en tu base de datos (puedes pasarlo por Intent)
                val clienteId = intent.getIntExtra("CLIENTE_ID", 1004)
                
                val listaComandas = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getComandas(clienteId)
                }

                if (listaComandas.isNotEmpty()) {
                    adapter = ComandaAdapter(listaComandas)
                    binding.rvComandas.adapter = adapter
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