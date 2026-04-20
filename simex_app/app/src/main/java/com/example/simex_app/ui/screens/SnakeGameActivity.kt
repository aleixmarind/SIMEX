package com.example.simex_app.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simex_app.R
import com.example.simex_app.databinding.ActivitySnakeGameBinding

class SnakeGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnakeGameBinding
    private val handler = Handler(Looper.getMainLooper())
    private var gameTickDelay = 200L
    private var clienteId: Int = -1
    private var nombreUsuario: String? = null

    private val gameTask = object : Runnable {
        override fun run() {
            binding.snakeView.step()
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
        binding.snakeView.setOnScoreUpdateListener { score ->
            binding.tvScore.text = "Paquetes: $score"
            // Aumentar velocidad cada 5 paquetes
            if (score > 0 && score % 5 == 0) {
                gameTickDelay = (gameTickDelay * 0.9).toLong().coerceAtLeast(100L)
            }
        }

        binding.snakeView.setOnGameOverListener {
            handler.removeCallbacks(gameTask)
            showGameOverDialog()
        }
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("¡Entrega Fallida!")
            .setMessage("Has chocado. Inténtalo de nuevo para completar la logística.")
            .setCancelable(false)
            .setPositiveButton("REINTENTAR") { _, _ ->
                gameTickDelay = 200L
                binding.snakeView.resetGame()
                binding.tvScore.text = "Paquetes: 0"
                handler.postDelayed(gameTask, gameTickDelay)
            }
            .setNegativeButton("SALIR") { _, _ -> finish() }
            .show()
    }

    private fun setupControls() {
        binding.btnUp.setOnClickListener { binding.snakeView.setDirection(SnakeView.Direction.UP) }
        binding.btnDown.setOnClickListener { binding.snakeView.setDirection(SnakeView.Direction.DOWN) }
        binding.btnLeft.setOnClickListener { binding.snakeView.setDirection(SnakeView.Direction.LEFT) }
        binding.btnRight.setOnClickListener { binding.snakeView.setDirection(SnakeView.Direction.RIGHT) }
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
                R.id.nav_juego -> true
                else -> {
                    // Otros menús si es necesario
                    false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gameTask)
    }
}