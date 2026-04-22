package com.example.simex_app.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import java.util.*

class SnakeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paintSnake = Paint().apply { color = Color.parseColor("#E21D25") } // brand_600 (Remolque)
    private val paintHead = Paint().apply { color = Color.parseColor("#BA2A30") } // brand_900 (Cabina)
    private val paintPackage = Paint().apply { color = Color.parseColor("#4B5563") } // gray_600 (Carga)
    private val paintGrid = Paint().apply { 
        color = Color.parseColor("#F3F4F6") // gray_100
        strokeWidth = 1f
    }
    private val paintBorder = Paint().apply {
        color = Color.parseColor("#111827") // gray_900
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    
    private var gridSize = 20
    private var cellSize = 0f
    
    private val snake = mutableListOf<Point>()
    private var direction = Direction.RIGHT
    private var packagePos = Point(10, 10)
    private val random = Random()
    
    private var onScoreUpdate: ((Int) -> Unit)? = null
    private var onGameOver: (() -> Unit)? = null
    
    private var isGameOver = false

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    init {
        resetGame()
    }

    fun setOnScoreUpdateListener(listener: (Int) -> Unit) {
        onScoreUpdate = listener
    }

    fun setOnGameOverListener(listener: () -> Unit) {
        onGameOver = listener
    }

    fun resetGame() {
        snake.clear()
        snake.add(Point(5, 10))
        snake.add(Point(4, 10))
        snake.add(Point(3, 10))
        direction = Direction.RIGHT
        spawnPackage()
        isGameOver = false
        invalidate()
    }

    fun setDirection(newDirection: Direction) {
        if ((direction == Direction.UP && newDirection != Direction.DOWN) ||
            (direction == Direction.DOWN && newDirection != Direction.UP) ||
            (direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
            direction = newDirection
        }
    }

    fun step() {
        if (isGameOver) return

        val head = snake[0]
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        // Colisión con límites (Recuadro)
        if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize) {
            endGame()
            return
        }

        // Colisión con uno mismo
        if (snake.contains(newHead)) {
            endGame()
            return
        }

        snake.add(0, newHead)

        // Recoger paquete
        if (newHead == packagePos) {
            onScoreUpdate?.invoke(snake.size - 3)
            spawnPackage()
        } else {
            snake.removeAt(snake.size - 1)
        }

        invalidate()
    }

    private fun spawnPackage() {
        var newPos: Point
        do {
            newPos = Point(random.nextInt(gridSize), random.nextInt(gridSize))
        } while (snake.contains(newPos))
        packagePos = newPos
    }

    private fun endGame() {
        isGameOver = true
        onGameOver?.invoke()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cellSize = width.toFloat() / gridSize

        // 1. Dibujar Rejilla para ver el espacio
        for (i in 0..gridSize) {
            canvas.drawLine(i * cellSize, 0f, i * cellSize, height.toFloat(), paintGrid)
            canvas.drawLine(0f, i * cellSize, width.toFloat(), i * cellSize, paintGrid)
        }

        // 2. Dibujar el Recuadro de límites (Lo que pediste)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBorder)

        // 3. Dibujar Paquete (Carga logística)
        canvas.drawRect(
            packagePos.x * cellSize + 6,
            packagePos.y * cellSize + 6,
            (packagePos.x + 1) * cellSize - 6,
            (packagePos.y + 1) * cellSize - 6,
            paintPackage
        )

        // 4. Dibujar Camión
        for (i in snake.indices) {
            val p = snake[i]
            val currentPaint = if (i == 0) paintHead else paintSnake
            
            // La cabina es un bloque más sólido, el remolque tiene márgenes
            val m = if (i == 0) 2f else 5f
            canvas.drawRect(
                p.x * cellSize + m,
                p.y * cellSize + m,
                (p.x + 1) * cellSize - m,
                (p.y + 1) * cellSize - m,
                currentPaint
            )
        }
    }
}