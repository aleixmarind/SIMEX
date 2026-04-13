package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.example.simex_app.databinding.DialogDecisionOfertaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfertasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfertasBinding
    private lateinit var adapter: OfertaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfertasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clienteId = intent.getIntExtra("CLIENTE_ID", 1004)
        val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Cliente"

        setupRecyclerView(clienteId, nombreUsuario)
        setupBottomNavigation(clienteId, nombreUsuario)
        obtenerOfertas(clienteId)
    }

    private fun setupRecyclerView(clienteId: Int, nombreUsuario: String) {
        binding.rvOfertas.layoutManager = LinearLayoutManager(this)
        adapter = OfertaAdapter(emptyList()) { oferta ->
            mostrarDialogoDecisionModerno(oferta, clienteId, nombreUsuario)
        }
        binding.rvOfertas.adapter = adapter
    }

    private fun mostrarDialogoDecisionModerno(oferta: Comanda, clienteId: Int, nombreUsuario: String) {
        val dialogBinding = DialogDecisionOfertaBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvDialogTitle.text = "Oferta #${oferta.id}"
        dialogBinding.tvDialogMessage.text = "¿Deseas aceptar o rechazar el envío a ${oferta.puertoDestino}?"

        // Acción ACEPTAR
        dialogBinding.btnAceptar.setOnClickListener {
            dialog.dismiss()
            enviarDecision(oferta.id, true, null, clienteId, nombreUsuario)
        }

        // Acción RECHAZAR (Muestra el campo de motivo)
        dialogBinding.btnRechazar.setOnClickListener {
            dialogBinding.tvDialogTitle.text = "Rechazar Oferta"
            dialogBinding.tvDialogMessage.text = "Por favor, indica el motivo del rechazo."
            dialogBinding.tilMotivo.visibility = View.VISIBLE
            dialogBinding.btnAceptar.visibility = View.GONE
            dialogBinding.btnRechazar.visibility = View.GONE
            dialogBinding.btnEnviarRechazo.visibility = View.VISIBLE
        }

        // Acción ENVIAR RECHAZO (Con validación)
        dialogBinding.btnEnviarRechazo.setOnClickListener {
            val motivo = dialogBinding.etMotivo.text.toString()
            if (motivo.isNotBlank()) {
                dialog.dismiss()
                enviarDecision(oferta.id, false, motivo, clienteId, nombreUsuario)
            } else {
                dialogBinding.tilMotivo.error = "El motivo es obligatorio"
            }
        }

        dialogBinding.btnCancelar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun enviarDecision(id: Int, aceptada: Boolean, motivo: String?, clienteId: Int, nombreUsuario: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.decidirOferta(id, DecisionOfertaDTO(aceptada, motivo))
                }
                
                if (response.isSuccessful) {
                    val msg = if (aceptada) "¡Oferta aceptada con éxito!" else "Oferta rechazada"
                    Toast.makeText(this@OfertasActivity, msg, Toast.LENGTH_SHORT).show()
                    obtenerOfertas(clienteId) 
                } else {
                    Toast.makeText(this@OfertasActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error", e)
                Toast.makeText(this@OfertasActivity, "Sin conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation(clienteId: Int, nombreUsuario: String) {
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
            }
        }
    }
}