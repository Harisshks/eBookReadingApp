package com.example.bookreaderapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

@Composable
fun PdfViewerScreen(pdfUrl: String, bookId: String) {
    val context = LocalContext.current
    var file by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pdfUrl) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                file = downloadPdfFile(context, pdfUrl)
                error = null
            } catch (e: Exception) {
                error = e.message ?: "Error loading PDF"
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error!!, color = Color.Red)
            }
        }
        file != null -> {
            PdfPager(file = file!!, bookId = bookId)
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No PDF file found", color = Color.Red)
            }
        }
    }
}

@Composable
fun PdfPager(file: File, bookId: String) {
    val context = LocalContext.current
    var pageCount by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(file) {
        try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    pageCount = renderer.pageCount
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error = "Failed to read PDF"
        }
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
        }
    } else if (pageCount > 0) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(pageCount) { pageIndex ->
                PdfPageItem(context = context, file = file, pageIndex = pageIndex)
                saveLastReadPage(context, bookId, pageIndex)
            }
        }

    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun PdfPageItem(context: Context, file: File, pageIndex: Int) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            bitmap = loadPdfPageAsBitmap(context, file, pageIndex)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "PDF Page $pageIndex",
            contentScale = ContentScale.FillWidth, // Fit to screen width
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


fun loadPdfPageAsBitmap(context: Context, file: File, pageIndex: Int): Bitmap {
    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)
    val page = renderer.openPage(pageIndex)

    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    page.close()
    renderer.close()
    fileDescriptor.close()

    return bitmap
}



private fun saveLastReadPage(context: Context, bookId: String, pageIndex: Int) {
    context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
        .edit()
        .putInt("last_page_$bookId", pageIndex)
        .apply()
}

private fun getLastReadPage(context: Context, bookId: String): Int {
    return context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
        .getInt("last_page_$bookId", 0)
}



private suspend fun downloadPdfFile(context: Context, url: String): File? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return@withContext null
            }

            val file = File.createTempFile("temp_pdf_", ".pdf", context.cacheDir)
            response.body?.byteStream()?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}