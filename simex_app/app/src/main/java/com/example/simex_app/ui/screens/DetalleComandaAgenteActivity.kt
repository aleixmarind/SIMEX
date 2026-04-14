package com.example.simex_app.ui.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.databinding.ActivityDetalleComandaAgenteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleComandaAgenteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleComandaAgenteBinding
    private var comandaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleComandaAgenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val comanda = intent.getParcelableExtra<Comanda>("DETALLE_COMANDA")
        
        comanda?.let { item ->
            comandaId = item.id
            binding.tvDetalleNumPedido.text = "PEDIDO #${item.numPedido ?: item.id}"
            binding.tvDetalleNombreOferta.text = item.nombreOferta ?: "Sin nombre"
            
            configurarTracking(item)

            binding.btnAvanzarEstado.setOnClickListener {
                avanzarTracking(item)
            }
        }
    }

    private fun configurarTracking(item: Comanda) {
        val pasos = item.pasosSeguimiento ?: emptyList()
        val ordenActual = pasos.find { it.id == item.trackingActualId }?.ordre ?: 0
        
        val trackingAdapter = TrackingAdapter(pasos, ordenActual)
        binding.rvTracking.apply {
            layoutManager = LinearLayoutManager(this@DetalleComandaAgenteActivity)
            adapter = trackingAdapter
        }
    }

    private fun avanzarTracking(item: Comanda) {
        val pasos = item.pasosSeguimiento ?: return
        val indexActual = pasos.indexOfFirst { it.id == item.trackingActualId }
        
        if (indexActual < pasos.size - 1) {
            val siguientePaso = pasos[indexActual + 1]
            
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.actualizarTracking(item.id, siguientePaso.id)
                    }
                    
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetalleComandaAgenteActivity, "Estado avanzado a: ${siguientePaso.nom}", Toast.LENGTH_SHORT).show()
                        // Recargamos los datos para que se vea el cambio (o cerramos y actualizamos el home)
                        finish() 
                    } else {
                        Toast.makeText(this@DetalleComandaAgenteActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("AGENTE_TRACKING", "Error", e)
                    Toast.makeText(this@DetalleComandaAgenteActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "La comanda ya ha llegado al paso final", Toast.LENGTH_SHORT).show()
        }
    }
}