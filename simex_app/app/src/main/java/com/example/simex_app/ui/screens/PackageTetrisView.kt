package com.example.simex_app.ui.screens

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.min

class PackageTetrisView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val ROWS = 20
    private val COLS = 10
    private var cellSize = 0f

    private val grid = Array(ROWS) { IntArray(COLS) { 0 } }
    private var currentPiece: Piece? = null
    private var isGameOver = false
    private var score = 0

    private val paintPackage = Paint().apply { 
        style = Paint.Style.FILL
        isAntiAlias = true 
    }
    private val paintBorder = Paint().apply {
        color = Color.parseColor("#111827")
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }
    private val paintGrid = Paint().apply {
        color = Color.parseColor("#374151") // Gray 700
        style = Paint.Style.STROKE
        strokeWidth = 2f
        alpha = 90
    }
    private val paintEmpty = Paint().apply {
        color = Color.parseColor("#F3F4F6") // gray_100
        style = Paint.Style.FILL
    }

    private val colors = intArrayOf(
        Color.parseColor("#E21D25"), // Brand Red
        Color.parseColor("#4B5563"), // Gray 600
        Color.parseColor("#059669"), // Green 600
        Color.parseColor("#2563EB"), // Blue 600
        Color.parseColor("#D97706")  // Orange 600
    )

    private var onScoreUpdate: ((Int) -> Unit)? = null
    private var onGameOver: (() -> Unit)? = null

    fun setOnScoreUpdateListener(listener: (Int) -> Unit) { onScoreUpdate = listener }
    fun setOnGameOverListener(listener: () -> Unit) { onGameOver = listener }

    init {
        spawnNewPiece()
    }

    fun resetGame() {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                grid[r][c] = 0
            }
        }
        score = 0
        isGameOver = false
        spawnNewPiece()
        invalidate()
    }

    private fun spawnNewPiece() {
        val type = Random().nextInt(7)
        val color = colors[Random().nextInt(colors.size)]
        val newPiece = Piece(type, color)
        
        // Centrar pieza horizontalmente
        newPiece.x = (COLS - newPiece.shape[0].size) / 2
        newPiece.y = 0

        if (checkCollision(newPiece)) {
            // Si la nueva pieza choca nada más salir, GAME OVER
            isGameOver = true
            onGameOver?.invoke()
        } else {
            currentPiece = newPiece
        }
    }

    fun moveLeft() { if (move(-1, 0)) invalidate() }
    fun moveRight() { if (move(1, 0)) invalidate() }
    
    fun moveDown(): Boolean {
        if (isGameOver) return false
        val p = currentPiece ?: return false
        
        // Intentar mover hacia abajo
        p.y += 1
        if (checkCollision(p)) {
            p.y -= 1
            lockPiece()
            
            // Sumar puntos por colocar paquete: +50
            score += 50
            onScoreUpdate?.invoke(score)
            
            clearRows()
            spawnNewPiece()
            invalidate()
            return false
        }
        invalidate()
        return true
    }

    fun rotate() {
        if (isGameOver) return
        val p = currentPiece ?: return
        val oldShape = p.shape
        val oldX = p.x
        p.rotate()
        
        // Wall kick: ajustar si la rotación sale del tablero o choca
        if (checkCollision(p)) {
            // Intentar empujar a la izquierda si se sale por la derecha
            if (p.x + p.shape[0].size > COLS) p.x = COLS - p.shape[0].size
            // Intentar empujar a la derecha si se sale por la izquierda
            if (p.x < 0) p.x = 0
            
            if (checkCollision(p)) {
                // Si sigue chocando, revertir rotación
                p.x = oldX
                p.shape = oldShape
            }
        }
        invalidate()
    }

    private fun move(dx: Int, dy: Int): Boolean {
        if (isGameOver) return false
        val p = currentPiece ?: return false
        p.x += dx
        p.y += dy
        if (checkCollision(p)) {
            p.x -= dx
            p.y -= dy
            return false
        }
        return true
    }

    private fun checkCollision(p: Piece): Boolean {
        for (r in p.shape.indices) {
            for (c in p.shape[r].indices) {
                if (p.shape[r][c] != 0) {
                    val nx = p.x + c
                    val ny = p.y + r
                    
                    // Límites del almacén (Suelo y Paredes)
                    if (nx < 0 || nx >= COLS || ny >= ROWS) return true
                    // Bloques ya colocados
                    if (ny >= 0 && grid[ny][nx] != 0) return true
                }
            }
        }
        return false
    }

    private fun lockPiece() {
        val p = currentPiece ?: return
        for (r in p.shape.indices) {
            for (c in p.shape[r].indices) {
                if (p.shape[r][c] != 0) {
                    val ny = p.y + r
                    val nx = p.x + c
                    if (ny in 0 until ROWS && nx in 0 until COLS) {
                        grid[ny][nx] = p.color
                    }
                }
            }
        }
        currentPiece = null
    }

    private fun clearRows() {
        var rowsCleared = 0
        var r = ROWS - 1
        while (r >= 0) {
            var full = true
            for (c in 0 until COLS) {
                if (grid[r][c] == 0) {
                    full = false
                    break
                }
            }
            if (full) {
                rowsCleared++
                // Desplazar filas superiores
                for (r2 in r downTo 1) {
                    grid[r2] = grid[r2 - 1].copyOf()
                }
                grid[0] = IntArray(COLS) { 0 }
            } else {
                r--
            }
        }

        if (rowsCleared > 0) {
            score += when (rowsCleared) {
                1 -> 200
                2 -> 500
                3 -> 1000
                4 -> 2000
                else -> rowsCleared * 500
            }
            onScoreUpdate?.invoke(score)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Ajustar tamaño de celda al espacio disponible
        cellSize = min(width.toFloat() / COLS, height.toFloat() / ROWS)
        
        val offsetX = (width - cellSize * COLS) / 2f
        val offsetY = (height - cellSize * ROWS) / 2f

        canvas.save()
        canvas.translate(offsetX, offsetY)

        // 1. Fondo del almacén
        canvas.drawRect(0f, 0f, cellSize * COLS, cellSize * ROWS, paintEmpty)

        // 2. Cuadrícula visible y marcada
        for (c in 0..COLS) {
            canvas.drawLine(c * cellSize, 0f, c * cellSize, ROWS * cellSize, paintGrid)
        }
        for (r in 0..ROWS) {
            canvas.drawLine(0f, r * cellSize, COLS * cellSize, r * cellSize, paintGrid)
        }

        // 3. Bloques fijos
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                val color = grid[r][c]
                if (color != 0) {
                    paintPackage.color = color
                    drawBox(canvas, c * cellSize, r * cellSize, paintPackage)
                }
            }
        }

        // 4. Paquete cayendo (Pieza activa)
        currentPiece?.let { p ->
            paintPackage.color = p.color
            for (r in p.shape.indices) {
                for (c in p.shape[r].indices) {
                    if (p.shape[r][c] != 0) {
                        drawBox(canvas, (p.x + c) * cellSize, (p.y + r) * cellSize, paintPackage)
                    }
                }
            }
        }

        // 5. Borde grueso del área de juego
        canvas.drawRect(0f, 0f, cellSize * COLS, cellSize * ROWS, paintBorder)
        
        canvas.restore()
    }

    private fun drawBox(canvas: Canvas, x: Float, y: Float, p: Paint) {
        val margin = cellSize * 0.1f
        val rect = RectF(x + margin, y + margin, x + cellSize - margin, y + cellSize - margin)
        canvas.drawRoundRect(rect, cellSize * 0.15f, cellSize * 0.15f, p)
        
        // Bordes de la caja
        val detailPaint = Paint().apply {
            color = Color.BLACK
            alpha = 60
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(rect, cellSize * 0.15f, cellSize * 0.15f, detailPaint)
        
        // Cinta de embalaje logística
        val tapePaint = Paint().apply {
            color = Color.parseColor("#EAB308")
            style = Paint.Style.FILL
            alpha = 170
        }
        val tapeW = cellSize * 0.15f
        // Cinta vertical
        canvas.drawRect(x + (cellSize - tapeW)/2, y + margin, x + (cellSize + tapeW)/2, y + cellSize - margin, tapePaint)
        // Cinta horizontal
        canvas.drawRect(x + margin, y + (cellSize - tapeW)/2, x + cellSize - margin, y + (cellSize + tapeW)/2, tapePaint)
    }

    class Piece(val type: Int, val color: Int) {
        var x = 0
        var y = 0
        var shape = getShapeByType(type)

        fun rotate() {
            val newShape = Array(shape[0].size) { IntArray(shape.size) }
            for (r in shape.indices) {
                for (c in shape[r].indices) {
                    newShape[c][shape.size - 1 - r] = shape[r][c]
                }
            }
            shape = newShape
        }

        companion object {
            fun getShapeByType(type: Int): Array<IntArray> {
                return when (type) {
                    0 -> arrayOf(intArrayOf(1, 1, 1, 1)) // I
                    1 -> arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)) // O
                    2 -> arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)) // T
                    3 -> arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)) // S
                    4 -> arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)) // Z
                    5 -> arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1)) // J
                    else -> arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1)) // L
                }
            }
        }
    }
}
