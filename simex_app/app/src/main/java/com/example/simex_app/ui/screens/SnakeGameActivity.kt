package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simex_app.R
import com.example.simex_app.databinding.ActivitySnakeGameBinding

class SnakeGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnakeGameBinding
    private val handler = Handler(Looper.getMainLooper())
    private var gameTickDelay = 100L
    private var clienteId: Int = -1
    private var nombreUsuario: String? = null

    private val gameTask = object : Runnable {
        override fun run() {
            binding.tetrisView.update()
            handler.postDelayed(this, gameTickDelay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnakeGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        nombreUsuario = intent.getStringExtra("USER_NAME")

        setupGame()
        setupControls()
        setupBottomNavigation()
        
        handler.postDelayed(gameTask, gameTickDelay)
    }

    private fun setupGame() {
        binding.tetrisView.setOnScoreUpdateListener { score ->
            binding.tvScore.text = "$score Puntos"
            if (score > 0 && score % 100 == 0) {
                gameTickDelay = (gameTickDelay * 0.95).toLong().coerceAtLeast(100L)
            }
        }

        binding.tetrisView.setOnGameOverListener {
            handler.removeCallbacks(gameTask)
            showGameOverDialog()
        }
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("¡Fin de la Partida!")
            .setMessage("La serpiente ha chocado. ¿Quieres intentarlo de nuevo?")
            .setCancelable(false)
            .setPositiveButton("REINTENTAR") { _, _ ->
                gameTickDelay = 100L
                binding.tetrisView.resetGame()
                binding.tvScore.text = "0 Puntos"
                handler.postDelayed(gameTask, gameTickDelay)
            }
            .setNegativeButton("SALIR") { _, _ -> finish() }
            .show()
    }

    private fun setupControls() {
        binding.btnLeft.setOnClickListener { binding.tetrisView.moveLeft() }
        binding.btnRight.setOnClickListener { binding.tetrisView.moveRight() }
        binding.btnRotate.setOnClickListener { binding.tetrisView.rotate() } // Arriba
        binding.btnDown.setOnClickListener { binding.tetrisView.setDirectionDown() } // Abajo
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                binding.tetrisView.moveLeft()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                binding.tetrisView.moveRight()
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                binding.tetrisView.rotate()
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                binding.tetrisView.setDirectionDown()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_juego
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    handler.removeCallbacks(gameTask)
                    val esAgente = intent.getBooleanExtra("ES_AGENTE", false)
                    val destination = if (esAgente) HomePageAgenteActivity::class.java else HomePageClienteActivity::class.java
                    val intent = Intent(this, destination)
                    intent.putExtra("CLIENTE_ID", clienteId)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    handler.removeCallbacks(gameTask)
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("CLIENTE_ID", clienteId)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_juego -> true
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gameTask)
    }
}
