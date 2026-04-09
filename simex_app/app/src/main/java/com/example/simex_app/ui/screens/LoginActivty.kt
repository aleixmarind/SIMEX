package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                ejecutarLogin(email, pass)
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ejecutarLogin(email: String, pass: String) {
        val datos = mapOf("usuari" to email, "contrasenya" to pass)

        lifecycleScope.launch {
            try {
                Log.d("LOGIN_DEBUG", "Intentando login para: $email")
                val response = RetrofitClient.instance.login(datos)

                if (response.isSuccessful) {
                    val loginRes = response.body()
                    Log.d("LOGIN_DEBUG", "Login exitoso. Respuesta: $loginRes")

                    when (loginRes?.tipo) {
                        "Agente" -> {
                            Log.d("LOGIN_DEBUG", "Redirigiendo a HomePageAgente")
                            val intent = Intent(this@LoginActivity, HomePageAgenteActivity::class.java)
                            intent.putExtra("USER_NAME", loginRes.nombre)
                            intent.putExtra("CLIENTE_ID", loginRes.id)
                            startActivity(intent)
                            finish()
                        }
                        "Cliente" -> {
                            Log.d("LOGIN_DEBUG", "Redirigiendo a HomePageCliente")
                            val intent = Intent(this@LoginActivity, HomePageClienteActivity::class.java)
                            intent.putExtra("USER_NAME", loginRes.nombre)
                            intent.putExtra("CLIENTE_ID", loginRes.id)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Log.e("LOGIN_DEBUG", "Rol desconocido o nulo: ${loginRes?.tipo}")
                            Toast.makeText(this@LoginActivity, "Rol desconocido: ${loginRes?.tipo}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOGIN_DEBUG", "Error en login. Código: ${response.code()}, Error: $errorBody")
                    Toast.makeText(this@LoginActivity, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LOGIN_DEBUG", "Error de red/excepción: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
