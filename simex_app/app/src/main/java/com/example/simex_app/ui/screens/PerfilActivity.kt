package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.simex_app.R
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.databinding.ActivityPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clienteId = intent.getIntExtra("CLIENTE_ID", 1004)
        val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Usuario"

        setupBottomNavigation(clienteId, nombreUsuario)
        obtenerDatosPerfil(clienteId)

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun obtenerDatosPerfil(id: Int) {
        lifecycleScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getUsuario(id)
                }

                binding.tvFullName.text = "${usuario.nom} ${usuario.cognoms}"
                binding.tvEmail.text = usuario.correu
                binding.tvUserId.text = "#${usuario.id}"
                
                // Determinamos el rol según el ID (1004 Cliente, 1005 Agente según tu SQL)
                binding.tvUserRole.text = if (usuario.rolId == 1004) "CLIENTE" else "AGENTE"

            } catch (e: Exception) {
                Log.e("PERFIL_ERROR", "Error al obtener perfil", e)
                Toast.makeText(this@PerfilActivity, "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation(clienteId: Int, nombreUsuario: String) {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Para saber a qué Home volver, miramos el texto del rol actual
                    val esAgente = binding.tvUserRole.text == "AGENTE"
                    val destination = if (esAgente) HomePageAgenteActivity::class.java else HomePageClienteActivity::class.java
                    
                    val intent = Intent(this, destination)
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
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }
}