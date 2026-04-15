package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simex_app.R
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.data.models.DecisionOfertaDTO
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.databinding.ActivityOfertasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfertasActivity : AppCompatActivity(), OnOfertaDecisionListener {

    private lateinit var binding: ActivityOfertasBinding
    private lateinit var adapter: OfertaAdapter
    private var clienteId: Int = -1
    private var nombreUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfertasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        nombreUsuario = intent.getStringExtra("USER_NAME")

        if (clienteId == -1) {
            Toast.makeText(this, "Error: No se ha encontrado el ID del cliente", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupBottomNavigation()
        obtenerOfertas(clienteId)
    }

    private fun setupRecyclerView() {
        binding.rvOfertas.layoutManager = LinearLayoutManager(this)
        adapter = OfertaAdapter(mutableListOf(), this)
        binding.rvOfertas.adapter = adapter
    }

    override fun onAceptar(oferta: Comanda) {
        AlertDialog.Builder(this)
            .setTitle("Aceptar Oferta")
            .setMessage("¿Estás seguro de que quieres aceptar la oferta #${oferta.id}?")
            .setPositiveButton("Sí") { _, _ ->
                enviarDecision(oferta, true, null)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onRechazar(oferta: Comanda) {
        val input = EditText(this)
        input.hint = "Motivo del rechazo"
        
        AlertDialog.Builder(this)
            .setTitle("Rechazar Oferta")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val motivo = input.text.toString()
                if (motivo.isNotBlank()) {
                    enviarDecision(oferta, false, motivo)
                } else {
                    Toast.makeText(this, "El motivo es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarDecision(oferta: Comanda, aceptada: Boolean, motivo: String?) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.decidirOferta(oferta.id, DecisionOfertaDTO(aceptada, motivo))
                }
                
                if (response.isSuccessful) {
                    val msg = if (aceptada) "¡Oferta aceptada con éxito!" else "Oferta rechazada"
                    Toast.makeText(this@OfertasActivity, msg, Toast.LENGTH_SHORT).show()
                    adapter.removeItem(oferta)
                } else {
                    Toast.makeText(this@OfertasActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error", e)
                Toast.makeText(this@OfertasActivity, "Sin conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
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
                R.id.nav_comandas -> {
                    val intent = Intent(this, ComandasActivity::class.java)
                    intent.putExtra("CLIENTE_ID", clienteId)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtenerOfertas(clienteId: Int) {
        lifecycleScope.launch {
            try {
                val lista = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getOfertasPendientes(clienteId)
                }
                adapter.updateList(lista)
            } catch (e: Exception) {
                Log.e("API", "Error", e)
                Toast.makeText(this@OfertasActivity, "Error al cargar ofertas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
