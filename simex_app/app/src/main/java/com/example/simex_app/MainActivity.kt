package com.example.simex_app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.simex_app.data.network.RetrofitClient



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCargar = findViewById<Button>(R.id.btnCargar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)

        btnCargar.setOnClickListener {
            // Iniciamos la petición en segundo plano
            lifecycleScope.launch {
                try {
                    // 1. Llamamos a la API a través del cliente que creamos
                    val listaPaissos = RetrofitClient.instance.getPaissos()

                    // 2. Volvemos al hilo principal para actualizar la interfaz
                    if (listaPaissos.isNotEmpty()) {
                        var textoMostrar = ""
                        listaPaissos.forEach { pais ->
                            textoMostrar += "🌍 ${pais.nom} (ID: ${pais.id})\n"
                            // Si tiene ciudades, las listamos también
                            pais.ciutats.forEach { ciudad ->
                                // Forzamos la lectura de la propiedad
                                val nombreCiudad = (ciudad as com.example.simex_app.data.models.Ciutat).nom
                                textoMostrar += "   📍 $nombreCiudad\n"
                            }
                            textoMostrar += "\n"
                        }
                        tvResultado.text = textoMostrar
                    } else {
                        tvResultado.text = "La base de datos está vacía."
                    }

                } catch (e: Exception) {
                    // 3. Si algo falla (IP mal, API apagada, etc.)
                    tvResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@MainActivity, "Fallo de conexión", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}