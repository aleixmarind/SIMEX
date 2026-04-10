package com.example.simex_app.ui.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.databinding.ActivityDetalleComandaBinding

class DetalleComandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleComandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleComandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Obtener la comanda del Intent
        val comanda = intent.getParcelableExtra<Comanda>("DETALLE_COMANDA")

        comanda?.let { item ->
            // Datos básicos
            binding.tvDetalleNumPedido.text = item.numPedido ?: "N/A"
            binding.tvDetalleEstado.text = item.estado?.uppercase() ?: "DESCONOCIDO"
            binding.tvDetalleNombreOferta.text = item.nombreOferta ?: "Sin nombre"
            binding.tvDetalleOrigen.text = item.puertoOrigen ?: "Origen"
            binding.tvDetalleDestino.text = item.puertoDestino ?: "Destino"
            binding.tvDetalleFecha.text = item.fechaEntrega ?: "Pendiente"

            // Configurar Tracking Timeline
            val pasos = item.pasosSeguimiento ?: emptyList()
            // Buscamos el 'ordre' del paso donde está actualmente comparando IDs
            val ordenActual = pasos.find { it.id == item.trackingActualId }?.ordre ?: 0

            val trackingAdapter = TrackingAdapter(pasos, ordenActual)
            binding.rvTracking.apply {
                layoutManager = LinearLayoutManager(this@DetalleComandaActivity)
                adapter = trackingAdapter
                isNestedScrollingEnabled = false // El Scroll ya lo maneja el NestedScrollView del layout
            }
        }
    }
}