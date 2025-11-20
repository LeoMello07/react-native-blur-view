// android/src/main/java/com/blurview/BlurView.kt
package com.blurview

import android.content.Context
import android.graphics.*
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

enum class FadeStyleAndroid {
    BOTTOM, TOP, LEFT, RIGHT;

    companion object {
        fun fromString(value: String?): FadeStyleAndroid {
            return when (value) {
                "bottom" -> BOTTOM
                "top" -> TOP
                "left" -> LEFT
                "right" -> RIGHT
                else -> BOTTOM
            }
        }
    }
}

class BlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var blurIntensity: Float = 10f
    private var saturationIntensity: Float = 1f
    private var fadePercent: Float = 0f
    private var fadeStyle: FadeStyleAndroid = FadeStyleAndroid.TOP
    private var blurStyle: String = "regular"

    private val colorMatrixPaint = Paint()
    private val fadePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var fadeShader: Shader? = null

    init {
        setWillNotDraw(false)
    }

    /** SETTERS — chamadas pelo Manager **/
    fun setBlurIntensity(value: Double) {
        blurIntensity = clamp(value.toFloat(), 0f, 100f)
        invalidate()
    }

    fun setSaturationIntensity(value: Float) {
        saturationIntensity = clamp(value, 0f, 3f)
        applySaturation()
        invalidate()
    }

    fun setFadePercent(value: Float) {
        fadePercent = clamp(value, 0f, 1f)
        updateFadeShader()
        invalidate()
    }

    fun setFadeStyle(value: String?) {
        fadeStyle = FadeStyleAndroid.fromString(value)
        updateFadeShader()
        invalidate()
    }

    fun setBlurStyle(value: String?) {
        blurStyle = value ?: "regular"
        invalidate()
    }

    /** Saturação (ColorMatrix) **/
    private fun applySaturation() {
        val s = saturationIntensity
        val inv = 1f - s

        val matrix = ColorMatrix(
            floatArrayOf(
                inv + s, inv, inv, 0f, 0f,
                inv, inv + s, inv, 0f, 0f,
                inv, inv, inv + s, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        colorMatrixPaint.colorFilter = ColorMatrixColorFilter(matrix)
    }

    /** Fade **/
    private fun updateFadeShader() {
        if (width == 0 || height == 0 || fadePercent <= 0f) {
            fadeShader = null
            return
        }

        val start = fadePercent
        val end = 1f - fadePercent

        fadeShader = when (fadeStyle) {
            FadeStyleAndroid.BOTTOM -> LinearGradient(0f, height * end, 0f, height.toFloat(), Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP)
            FadeStyleAndroid.TOP -> LinearGradient(0f, 0f, 0f, height * start, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP)
            FadeStyleAndroid.LEFT -> LinearGradient(0f, 0f, width * start, 0f, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP)
            FadeStyleAndroid.RIGHT -> LinearGradient(width * end, 0f, width.toFloat(), 0f, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP)
        }

        fadePaint.shader = fadeShader
        fadePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    /** Renderização **/
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return  

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val offscreen = Canvas(bitmap)
        super.dispatchDraw(offscreen)

        val radius = (blurIntensity / 2f).coerceAtLeast(0.1f)
        val effect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)

        val node = RenderNode("blurNode")
        node.setPosition(0, 0, width, height)
        node.setRenderEffect(effect)

        val nCanvas = node.beginRecording()
        nCanvas.drawBitmap(bitmap, 0f, 0f, null)
        node.endRecording()

        canvas.drawRenderNode(node)
        canvas.drawPaint(colorMatrixPaint)

        fadeShader?.let {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fadePaint)
        }
    }

    private fun clamp(v: Float, min: Float, max: Float) = maxOf(min, minOf(max, v))
}
