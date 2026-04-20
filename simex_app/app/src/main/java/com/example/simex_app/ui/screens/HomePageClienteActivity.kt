package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.R
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.data.models.DecisionOfertaDTO
import com.example.simex_app.data.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageClienteActivity : AppCompatActivity(), OnOfertaDecisionListener {

    private lateinit var adapterComandas: ComandaAdapter
    private lateinit var adapterOfertas: OfertaAdapter
    
    private lateinit var rvRecentOrders: RecyclerView
    private lateinit var rvOfertasPendientes: RecyclerView
    private lateinit var cardNoOrders: MaterialCardView
    private lateinit var cardNoOffers: MaterialCardView

    private var clienteId: Int = -1
    private var nombreUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepagecliente)

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        nombreUsuario = intent.getStringExtra("USER_NAME")

        if (clienteId == -1) {
            Toast.makeText(this, "Error: No se ha encontrado el ID del cliente", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvWelcomeName = findViewById<TextView>(R.id.tvWelcomeName)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        rvRecentOrders = findViewById(R.id.rvRecentOrders)
        rvOfertasPendientes = findViewById(R.id.rvOfertasPendientes)
        cardNoOrders = findViewById(R.id.cardNoOrders)
        cardNoOffers = findViewById(R.id.cardNoOffers)

        tvWelcomeName.text = if (nombreUsuario != null) "Hola, $nombreUsuario" else "Hola"

        setupRecyclerViews()
        obtenerDatos(clienteId)

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
                    val intentJuego = Intent(this, SnakeGameActivity::class.java)
                    intentJuego.putExtra("CLIENTE_ID", clienteId)
                    intentJuego.putExtra("USER_NAME", nombreUsuario)
                    intentJuego.putExtra("ES_AGENTE", false)
                    startActivity(intentJuego)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("CLIENTE_ID", clienteId)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun setupRecyclerViews() {
        rvRecentOrders.layoutManager = LinearLayoutManager(this)
        adapterComandas = ComandaAdapter(emptyList()) { comanda ->
            val intent = Intent(this, DetalleComandaActivity::class.java)
            intent.putExtra("DETALLE_COMANDA", comanda)
            startActivity(intent)
        }
        rvRecentOrders.adapter = adapterComandas

        rvOfertasPendientes.layoutManager = LinearLayoutManager(this)
        adapterOfertas = OfertaAdapter(mutableListOf(), this)
        rvOfertasPendientes.adapter = adapterOfertas
    }

    private fun obtenerDatos(clienteId: Int) {
        lifecycleScope.launch {
            obtenerComandasRecientes(clienteId)
            obtenerOfertasPendientes(clienteId)
        }
    }

    private suspend fun obtenerComandasRecientes(clienteId: Int) {
        try {
            val listaComandas = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getComandas(clienteId)
            }
            if (listaComandas.isNotEmpty()) {
                adapterComandas.updateList(listaComandas.take(2))
                rvRecentOrders.visibility = View.VISIBLE
                cardNoOrders.visibility = View.GONE
            } else {
                rvRecentOrders.visibility = View.GONE
                cardNoOrders.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e("HOME_API_ERROR", "Error comandas", e)
        }
    }

    private suspend fun obtenerOfertasPendientes(clienteId: Int) {
        try {
            val listaOfertas = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getOfertasPendientes(clienteId)
            }
            if (listaOfertas.isNotEmpty()) {
                adapterOfertas.updateList(listaOfertas)
                rvOfertasPendientes.visibility = View.VISIBLE
                cardNoOffers.visibility = View.GONE
            } else {
                rvOfertasPendientes.visibility = View.GONE
                cardNoOffers.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e("HOME_API_ERROR", "Error ofertas", e)
        }
    }

    override fun onAceptar(oferta: Comanda) {
        AlertDialog.Builder(this)
            .setTitle("Aceptar Oferta")
            .setMessage("¿Estás seguro de que quieres aceptar esta oferta?")
            .setPositiveButton("Sí") { _, _ ->
                enviarDecision(oferta, true, null)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onRechazar(oferta: Comanda) {
        val input = EditText(this)
        input.hint = "Escribe el motivo del rechazo"
        
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
                    Toast.makeText(this@HomePageClienteActivity, "Decisión enviada con éxito", Toast.LENGTH_SHORT).show()
                    adapterOfertas.removeItem(oferta)
                    if (adapterOfertas.itemCount == 0) {
                        rvOfertasPendientes.visibility = View.GONE
                        cardNoOffers.visibility = View.VISIBLE
                    }
                    if (aceptada) {
                        obtenerComandasRecientes(clienteId)
                    }
                } else {
                    Toast.makeText(this@HomePageClienteActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomePageClienteActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}