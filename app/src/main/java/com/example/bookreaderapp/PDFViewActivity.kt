package com.example.bookreaderapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.ScaleGestureDetector
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class PDFViewActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private var pdfRenderer: PdfRenderer? = null
    private var currentFile: File? = null
    private var scaleFactor = 1f // Initial scale factor
    private lateinit var scaleDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }
        setContentView(progressBar)

        val pdfUrl = intent.getStringExtra("pdfUrl") ?: return

        // Set up the ScaleGestureDetector
        scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.1f, 5f) // Limit zoom scale
                return true
            }
        })

        lifecycleScope.launch {
            try {
                currentFile = withContext(Dispatchers.IO) { downloadPdfFile(pdfUrl) }
                currentFile?.let { file ->
                    val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    pdfRenderer = PdfRenderer(descriptor)

                    val displayWidth = resources.displayMetrics.widthPixels
                    val displayHeight = resources.displayMetrics.heightPixels

                    val bitmaps = withContext(Dispatchers.IO) {
                        (0 until pdfRenderer!!.pageCount).map { i ->
                            pdfRenderer!!.openPage(i).use { page ->
                                val scaleFactor = minOf(displayWidth.toFloat() / page.width, displayHeight.toFloat() / page.height)
                                val scaledWidth = (page.width * scaleFactor).toInt()
                                val scaledHeight = (page.height * scaleFactor).toInt()

                                val bmp = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                bmp
                            }
                        }
                    }

                    val bookId = intent.getStringExtra("bookId") ?: return@launch

                    val adapter = PdfPagerAdapter(this@PDFViewActivity, bitmaps, bookId)

                    viewPager = ViewPager2(this@PDFViewActivity).apply {
                        this.adapter = adapter

                        val prefs = getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
                        val lastPage = prefs.getInt("last_page_$bookId", 0)
                        setCurrentItem(lastPage, false)
                    }

                    setContentView(viewPager)


                    // Set a custom touch listener to handle zoom gestures
                    viewPager.setOnTouchListener { _, event ->
                        scaleDetector.onTouchEvent(event)
                        val matrix = Matrix()
                        matrix.setScale(scaleFactor, scaleFactor)
                        viewPager.scaleX = scaleFactor
                        viewPager.scaleY = scaleFactor
                        true
                    }
                } ?: run {
                    showError("Failed to download PDF.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Error loading PDF: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun downloadPdfFile(url: String): File? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val file = File(cacheDir, "temp_${System.currentTimeMillis()}.pdf")
                file.outputStream().use { response.body?.byteStream()?.copyTo(it) }
                file
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer?.close()
        currentFile?.delete()
    }
}
