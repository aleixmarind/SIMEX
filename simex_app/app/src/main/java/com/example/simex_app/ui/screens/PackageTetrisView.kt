package com.example.simex_app.ui.screens

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

class PackageTetrisView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val ROWS = 20
    private val COLS = 20
    private var cellSize = 0f

    private val snake = mutableListOf<Point>()
    private var food = Point(5, 5)
    private var direction = "RIGHT"
    private var isGameOver = false
    private var score = 0

    private val paintSnake = Paint().apply { color = Color.parseColor("#E21D25") }
    private val paintFood = Paint().apply { color = Color.parseColor("#10B981") }
    private val paintGrid = Paint().apply { 
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = -1f
    }

    private var onScoreUpdate: ((Int) -> Unit)? = null
    private var onGameOver: (() -> Unit)? = null

    fun setOnScoreUpdateListener(l: (Int) -> Unit) { onScoreUpdate = l }
    fun setOnGameOverListener(l: () -> Unit) { onGameOver = l }

    init {
        resetGame()
    }

    fun resetGame() {
        snake.clear()
        snake.add(Point(10, 10))
        snake.add(Point(9, 10))
        snake.add(Point(8, 10))
        direction = "RIGHT"
        score = 0
        isGameOver = false
        spawnFood()
        invalidate()
    }

    private fun spawnFood() {
        val r = Random()
        do {
            food = Point(r.nextInt(COLS), r.nextInt(ROWS))
        } while (snake.contains(food))
    }

    fun moveLeft() { if (direction != "RIGHT") direction = "LEFT" }
    fun moveRight() { if (direction != "LEFT") direction = "RIGHT" }
    fun rotate() { if (direction != "DOWN") direction = "UP" }
    fun moveDown() { update() }

    fun setDirectionDown() { if (direction != "UP") direction = "DOWN" }

    fun update() {
        if (isGameOver) return

        val head = snake[0]
        val newHead = when (direction) {
            "UP" -> Point(head.x, head.y - 1)
            "DOWN" -> Point(head.x, head.y + 1)
            "LEFT" -> Point(head.x - 1, head.y)
            else -> Point(head.x + 1, head.y)
        }

        if (newHead.x < 0 || newHead.x >= COLS || newHead.y < 0 || newHead.y >= ROWS || snake.contains(newHead)) {
            isGameOver = true
            onGameOver?.invoke()
            return
        }

        snake.add(0, newHead)

        if (newHead == food) {
            score += 10
            onScoreUpdate?.invoke(score)
            spawnFood()
        } else {
            snake.removeAt(snake.size - 1)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return
        cellSize = width.toFloat() / COLS

        for (i in 0..COLS) canvas.drawLine(i * cellSize, 0f, i * cellSize, height.toFloat(), paintGrid)
        for (i in 0..ROWS) canvas.drawLine(0f, i * cellSize, width.toFloat(), i * cellSize, paintGrid)

        canvas.drawRect(
            food.x * cellSize + 4, food.y * cellSize + 4,
            (food.x + 1) * cellSize - 4, (food.y + 1) * cellSize - 4,
            paintFood
        )

        for (p in snake) {
            canvas.drawRect(
                p.x * cellSize + 2, p.y * cellSize + 2,
                (p.x + 1) * cellSize - 2, (p.y + 1) * cellSize - 2,
                paintSnake
            )
        }
    }

    data class Point(val x: Int, val y: Int)
}
