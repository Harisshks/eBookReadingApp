package com.example.bookreaderapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewTreeObserver
import kotlin.math.min

class PdfPagerAdapter(
    private val context: Context,
    private val bitmaps: List<Bitmap>,
    private val bookId: String
) : RecyclerView.Adapter<PdfPagerAdapter.PdfViewHolder>() {

    inner class PdfViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        val matrix = Matrix()
        var scaleFactor = 1f
        val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 5.0f)
                matrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                imageView.imageMatrix = matrix
                return true
            }
        })

        init {
            imageView.setOnTouchListener { _, event ->
                scaleGestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.MATRIX
        }
        return PdfViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val bitmap = bitmaps[position]
        val imageView = holder.imageView

        imageView.setImageBitmap(bitmap)

        // Center and scale the image after layout is measured
        imageView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val viewWidth = imageView.width.toFloat()
                    val viewHeight = imageView.height.toFloat()
                    val bitmapWidth = bitmap.width.toFloat()
                    val bitmapHeight = bitmap.height.toFloat()

                    // Scale the image to fit in the center of the screen
                    val scale = min(viewWidth / bitmapWidth, viewHeight / bitmapHeight)
                    val dx = (viewWidth - bitmapWidth * scale) / 2f
                    val dy = (viewHeight - bitmapHeight * scale) / 2f

                    // Reset matrix and apply scale + translation
                    holder.matrix.reset()
                    holder.matrix.postScale(scale, scale)
                    holder.matrix.postTranslate(dx, dy)

                    imageView.imageMatrix = holder.matrix
                    holder.scaleFactor = scale // Save this as the baseline zoom for the page
                }
            }
        )

        // Ensure the image stays centered on zoom
        imageView.imageMatrix = holder.matrix

        // Save reading progress (optional)
        saveLastReadPage(bookId, position)
    }

    override fun getItemCount(): Int = bitmaps.size

    private fun saveLastReadPage(bookId: String, pageIndex: Int) {
        val prefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
        prefs.edit().putInt("last_page_$bookId", pageIndex).apply()
    }
}
