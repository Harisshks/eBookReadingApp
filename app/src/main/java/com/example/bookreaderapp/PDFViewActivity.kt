package com.example.bookreaderapp



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class PDFViewActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private var pdfRenderer: PdfRenderer? = null
    private var currentFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }
        setContentView(progressBar)

        val pdfUrl = intent.getStringExtra("pdfUrl") ?: return

        lifecycleScope.launch {
            try {
                currentFile = withContext(Dispatchers.IO) { downloadPdfFile(pdfUrl) }
                currentFile?.let { file ->
                    val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    pdfRenderer = PdfRenderer(descriptor)

                    val bitmaps = withContext(Dispatchers.IO) {
                        (0 until pdfRenderer!!.pageCount).map { i ->
                            pdfRenderer!!.openPage(i).use { page ->
                                val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                bmp
                            }
                        }
                    }

                    viewPager = ViewPager2(this@PDFViewActivity).apply {
                        adapter = PdfPagerAdapter(bitmaps)
                    }
                    setContentView(viewPager)
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
