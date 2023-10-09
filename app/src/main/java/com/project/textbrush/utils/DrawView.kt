package com.project.textbrush.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlin.math.atan


class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var currentPath: Path? = null
    private val defaultString = "Text Brush"
    private var text = ""
    private var currentLine = ArrayList<PointF>()
    val actionCounter = MutableLiveData(0)
    private var visibleTexts = ArrayList<ArrayList<Letter>>()
    private var textCurrentIndex = 0
    private val paint = Paint().apply {
        strokeWidth = 50f
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.MITER
        strokeCap = Paint.Cap.ROUND
        color = Color.GRAY
        alpha = 10
    }

    private val letterPaint = Paint().apply {
        textSize = 70f
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
    }

    private data class Letter(val value: Char, val x: Float, val y: Float, val angle: Double)

    companion object {
        private const val TAG = "DrawView"
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initPath(x, y)
            }

            MotionEvent.ACTION_MOVE -> {
                updatePath(x, y)
                updateLetters()
            }

            MotionEvent.ACTION_UP -> {
                textCurrentIndex = 0
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentPath?.let {
            canvas.drawPath(it, paint)
        }
        for (letters in visibleTexts) {
            for (letter in letters) {
                canvas.rotate(letter.angle.toFloat(), letter.x, letter.y)
                canvas.drawText(letter.value.toString(), letter.x, letter.y, letterPaint)
                canvas.rotate(-letter.angle.toFloat(), letter.x, letter.y)

            }
        }
    }

    private fun initPath(x: Float, y: Float) {
        Log.d(TAG, "initPath:")
        currentPath = Path()
        visibleTexts.add(ArrayList())
        actionCounter.value = (actionCounter.value ?: 0) + 1
        currentLine.clear()
        currentPath?.moveTo(x, y)
        currentLine.add(PointF(x, y))

    }

    private fun updatePath(x: Float, y: Float) {
        currentPath?.lineTo(x, y)
        currentLine.add(PointF(x, y))
    }

    private fun getText(): String {
        return text.ifEmpty {
            defaultString
        }
    }

    private fun getCurrentLetter(): Char {
        if (textCurrentIndex >= getText().length) {
            textCurrentIndex = 0
        }
        val value = getText()[textCurrentIndex]
        Log.d(TAG, "getCurrentLetter: value:${value}")
        textCurrentIndex++
        return value
    }

    fun undoLast() {
        visibleTexts.removeLast()
        if ((actionCounter.value ?: 0) > 0) {
            actionCounter.value = (actionCounter.value ?: 0) - 1
        }
        invalidate()
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setTextColor(color: Int) {
        letterPaint.color = color
    }

    fun setFamilyFont(font: String) {
        letterPaint.typeface = Typeface.create(
            font,
            Typeface.NORMAL
        )
    }

    private fun updateLetters() {
        if (currentLine.size >= 3) {
            val point1 = currentLine[0]
            val point2 = currentLine[1]
            val point3 = currentLine[2]

            val angleDegrees = getAngleBetweenPoints(point1, point2)

            val letter = getCurrentLetter()
            val textWidth: Float = letterPaint.measureText(letter.toString())

            visibleTexts.last().add(
                Letter(
                    letter,
                    point1.x + (textWidth / 2),
                    point1.y + (textWidth / 2),
                    angleDegrees
                )
            )
            currentLine.clear()
            currentLine.add(point3)
        }
    }

    private fun getAngleBetweenPoints(point1: PointF, point2: PointF): Double {
        val x = point2.x - point1.x
        val y = point2.y - point1.y
        val angleRadians = atan(y / x)
        return Math.toDegrees(angleRadians.toDouble())
    }

}