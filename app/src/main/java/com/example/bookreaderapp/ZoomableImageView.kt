package com.example.bookreaderapp


import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), ScaleGestureDetector.OnScaleGestureListener {

    private val detector = ScaleGestureDetector(context, this)
    private var scaleFactor = 1f

    init {
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        scaleFactor = scaleFactor.coerceIn(1f, 5f)
        val matrix = Matrix()
        matrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
        imageMatrix = matrix
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}
}
