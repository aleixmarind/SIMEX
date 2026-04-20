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

    private val paintSnake = Paint().apply { color = Color.parseColor("#E21D25") } // brand_600
    private val paintHead = Paint().apply { color = Color.parseColor("#BA2A30") } // brand_900
    private val paintPackage = Paint().apply { color = Color.parseColor("#6B7280") } // gray_500
    
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

        // Check wall collision
        if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize) {
            endGame()
            return
        }

        // Check self collision
        if (snake.contains(newHead)) {
            endGame()
            return
        }

        snake.add(0, newHead)

        // Check package collision
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

        // Draw package
        canvas.drawRect(
            packagePos.x * cellSize + 2,
            packagePos.y * cellSize + 2,
            (packagePos.x + 1) * cellSize - 2,
            (packagePos.y + 1) * cellSize - 2,
            paintPackage
        )

        // Draw snake
        for (i in snake.indices) {
            val p = snake[i]
            val currentPaint = if (i == 0) paintHead else paintSnake
            
            // Visual logic: First 3 segments look like a van, more segments look like a truck
            canvas.drawRect(
                p.x * cellSize + 1,
                p.y * cellSize + 1,
                (p.x + 1) * cellSize - 1,
                (p.y + 1) * cellSize - 1,
                currentPaint
            )
        }
    }
}