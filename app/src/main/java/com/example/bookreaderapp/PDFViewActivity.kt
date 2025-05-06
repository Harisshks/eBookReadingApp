package com.example.bookreaderapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import com.google.accompanist.pager.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

// Main Viewer Screen
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PdfViewerScreen(pdfUrl: String, bookId: String) {
    val context = LocalContext.current
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    LaunchedEffect(pdfUrl) {
        val file = downloadPdfFile(context, pdfUrl)
        file?.let {
            val descriptor = ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(descriptor)
            val displayWidth = context.resources.displayMetrics.widthPixels
            val displayHeight = context.resources.displayMetrics.heightPixels

            val pages = (0 until renderer.pageCount).map { i ->
                renderer.openPage(i).use { page ->
                    val scaleFactor = minOf(
                        displayWidth.toFloat() / page.width,
                        displayHeight.toFloat() / page.height
                    )
                    val scaledWidth = (page.width * scaleFactor).toInt()
                    val scaledHeight = (page.height * scaleFactor).toInt()
                    val bmp = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bmp
                }
            }
            bitmaps = pages
        }
    }

    if (bitmaps.isNotEmpty()) {
        PdfPager(bitmaps = bitmaps, bookId = bookId)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    }
}

// Horizontal Pager with saved page
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PdfPager(bitmaps: List<Bitmap>, bookId: String) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = getLastReadPage(context, bookId),
        pageCount = { bitmaps.size }
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        SaveLastReadPage(context, bookId, pageIndex)
        ZoomableImage(bitmap = bitmaps[pageIndex])
    }
}

// Zoomable PDF page
@Composable
fun ZoomableImage(bitmap: Bitmap) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    )
}

// SharedPreferences: Save
fun SaveLastReadPage(context: Context, bookId: String, pageIndex: Int) {
    val prefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
    prefs.edit().putInt("last_page_$bookId", pageIndex).apply()
}

// SharedPreferences: Get
fun getLastReadPage(context: Context, bookId: String): Int {
    val prefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
    return prefs.getInt("last_page_$bookId", 0)
}

// PDF Downloader from URL
suspend fun downloadPdfFile(context: Context, url: String): File? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.pdf")
            file.outputStream().use { response.body?.byteStream()?.copyTo(it) }
            file
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
