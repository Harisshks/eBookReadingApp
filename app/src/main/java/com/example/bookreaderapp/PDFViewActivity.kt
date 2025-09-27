//package com.example.bookreaderapp
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.os.ParcelFileDescriptor
//import android.util.LruCache
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.gestures.draggable
//import androidx.compose.foundation.gestures.rememberDraggableState
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlinx.coroutines.withContext
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.io.File
//import java.io.IOException
//import kotlin.math.roundToInt
//import androidx.core.graphics.createBitmap
//import androidx.core.content.edit
//
//// ------------------------------------------------------------
//// Entry point: downloads PDF, builds renderer, shows reader
//// ------------------------------------------------------------
//@Composable
//fun PdfViewerScreen(
//    pdfUrl: String,
//    bookId: String,
//    pageSpacing: Dp = 12.dp,
//    pageCornerRadius: Dp = 10.dp,
//) {
//    val context = LocalContext.current
//    var file by remember { mutableStateOf<File?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    var error by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(pdfUrl) {
//        isLoading = true
//        withContext(Dispatchers.IO) {
//            try {
//                file = downloadPdfFile(context, pdfUrl)
//                error = if (file == null) "Failed to download PDF" else null
//            } catch (e: Exception) {
//                error = e.message ?: "Error loading PDF"
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    when {
//        isLoading -> LoadingView()
//        error != null -> ErrorView(error!!)
//        file != null -> {
//            val manager = remember { PdfRendererManager(file!!) }
//            DisposableEffect(Unit) { onDispose { manager.close() } }
//
//            PdfReaderScaffold(
//                manager = manager,
//                bookId = bookId,
//                pageSpacing = pageSpacing,
//                pageCornerRadius = pageCornerRadius
//            )
//        }
//        else -> ErrorView("No PDF file found")
//    }
//}
//
//// ------------------------------------------------------------
//// Continuous vertical reader with pinch & double-tap zoom
//// ------------------------------------------------------------
//@Composable
//fun ContinuousPdfReader(
//    manager: PdfRendererManager,
//    bookId: String,
//    pageSpacing: Dp,
//    pageCornerRadius: Dp,
//    listState: LazyListState,
//    scale: Float,
//    onScaleChange: (Float) -> Unit,
//    onToggleUi: () -> Unit,
//) {
//    val context = LocalContext.current
//    val pageCount = remember { manager.pageCount }
//
//    val doubleTapGesture = Modifier.pointerInput(scale) {
//        detectTapGestures(
//            onTap = { onToggleUi() },
//            onDoubleTap = {
//                val newScale = if (scale > 1f) 1f else 2f
//                onScaleChange(newScale)
//            }
//        )
//    }
//
//    val pinchZoomGesture = Modifier.pointerInput(scale) {
//        detectTransformGestures { _, _, zoom, _ ->
//            val newScale = (scale * zoom).coerceIn(1f, 5f)
//            onScaleChange(newScale)
//        }
//    }
//
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .distinctUntilChanged()
//            .collect { index ->
//                saveLastReadPage(context, bookId, index)
//            }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFAF9F7))
//            .then(doubleTapGesture)
//            .then(pinchZoomGesture)
//    ) {
//        LazyColumn(
//            state = listState,
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(pageSpacing),
//            contentPadding = PaddingValues(vertical = 12.dp)
//        ) {
//            items(count = pageCount, key = { it }) { pageIndex ->
//                PdfPageCard(
//                    manager = manager,
//                    pageIndex = pageIndex,
//                    cornerRadius = pageCornerRadius,
//                    scale = scale
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun PdfPageCard(
//    manager: PdfRendererManager,
//    pageIndex: Int,
//    cornerRadius: Dp,
//    scale: Float,
//) {
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//    var containerWidthPx by remember { mutableIntStateOf(0) }
//    val density = LocalDensity.current
//    var offsetX by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(pageIndex, containerWidthPx, scale) {
//        if (containerWidthPx > 0) {
//            bitmap = manager.renderPageToWidth(pageIndex, (containerWidthPx * scale).toInt())
//        }
//    }
//
//    val baseHeightPx = remember(bitmap, scale) {
//        val b = bitmap
//        if (b != null && scale > 0f) (b.height / scale).toInt() else 0
//    }
//    val scaledHeightDp = with(density) { (baseHeightPx * scale).toDp() }
//
//    val maxTranslateXPx = remember(containerWidthPx, scale) {
//        val scaledWidth = containerWidthPx * scale
//        val extra = (scaledWidth - containerWidthPx).coerceAtLeast(0f)
//        extra / 2f
//    }
//
//    LaunchedEffect(scale, containerWidthPx) {
//        offsetX = if (scale <= 1f || containerWidthPx == 0) {
//            0f
//        } else {
//            offsetX.coerceIn(-maxTranslateXPx, maxTranslateXPx)
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .onGloballyPositioned { coordinates ->
//                containerWidthPx = coordinates.size.width
//            }
//            .height(scaledHeightDp),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .draggable(
//                    orientation = Orientation.Horizontal,
//                    state = rememberDraggableState { delta ->
//                        if (scale > 1f) {
//                            val newX = (offsetX + delta).coerceIn(-maxTranslateXPx, maxTranslateXPx)
//                            offsetX = newX
//                        }
//                    },
//                    enabled = scale > 1f
//                )
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    translationX = offsetX
//                ),
//            shape = RoundedCornerShape(cornerRadius),
//            elevation = CardDefaults.cardElevation(4.dp)
//        ) {
//            bitmap?.let {
//                Image(
//                    bitmap = it.asImageBitmap(),
//                    contentDescription = "Page $pageIndex",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFFEFEFC)),
//                    contentScale = ContentScale.FillWidth
//                )
//            }
//        }
//    }
//}
//
//class PdfRendererManager(file: File) {
//    private val fileDescriptor: ParcelFileDescriptor =
//        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//
//    private val pdfRenderer = PdfRenderer(fileDescriptor)
//    private val cache = LruCache<String, Bitmap>(50)
//    private val renderMutex = Mutex()
//
//    val pageCount: Int
//        get() = pdfRenderer.pageCount
//
//    suspend fun renderPageToWidth(index: Int, targetWidthPx: Int): Bitmap {
//        val safeWidth = targetWidthPx.coerceAtLeast(1)
//        val key = "$index@$safeWidth"
//
//        cache.get(key)?.let { return it }
//
//        return withContext(Dispatchers.IO) {
//            renderMutex.withLock {
//                pdfRenderer.openPage(index).use { page ->
//                    val scale = safeWidth.toFloat() / page.width.toFloat()
//                    val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)
//
//                    val bitmap = createBitmap(safeWidth, targetHeight)
//                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//
//                    cache.put(key, bitmap)
//                    bitmap
//                }
//            }
//        }
//    }
//
//    fun close() {
//        cache.evictAll()
//        pdfRenderer.close()
//        fileDescriptor.close()
//    }
//}
//
//private fun saveLastReadPage(context: Context, bookId: String, pageIndex: Int) {
//    context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
//        .edit {
//            putInt("last_page_$bookId", pageIndex)
//        }
//}
//
//private fun getLastReadPage(context: Context, bookId: String): Int {
//    return context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
//        .getInt("last_page_$bookId", 0)
//}
//
//private suspend fun downloadPdfFile(context: Context, url: String): File? =
//    withContext(Dispatchers.IO) {
//        try {
//            val client = OkHttpClient()
//            val request = Request.Builder().url(url).build()
//            client.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) return@withContext null
//
//                val file = File.createTempFile("temp_pdf_", ".pdf", context.cacheDir)
//                response.body?.byteStream()?.use { input ->
//                    file.outputStream().use { output -> input.copyTo(output) }
//                }
//                file
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//@Composable
//fun LoadingView() {
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        CircularProgressIndicator()
//    }
//}
//
//@Composable
//fun ErrorView(message: String) {
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text(text = message, color = Color.Red)
//    }
//}
//
//// ------------------------------------------------------------
//// Scaffold wrapper: restores last page + zoom state
//// ------------------------------------------------------------
//@Composable
//fun PdfReaderScaffold(
//    manager: PdfRendererManager,
//    bookId: String,
//    pageSpacing: Dp,
//    pageCornerRadius: Dp,
//) {
//    val context = LocalContext.current
//    val pageCount = remember { manager.pageCount }
//    val savedIndex = remember { getLastReadPage(context, bookId).coerceIn(0, pageCount - 1) }
//    val listState = rememberLazyListState(initialFirstVisibleItemIndex = savedIndex)
//
//    var scale by remember { mutableFloatStateOf(1.2f) }
//
//    ContinuousPdfReader(
//        manager = manager,
//        bookId = bookId,
//        pageSpacing = pageSpacing,
//        pageCornerRadius = pageCornerRadius,
//        listState = listState,
//        scale = scale,
//        onScaleChange = { scale = it },
//        onToggleUi = { }
//    )
//}

package com.example.bookreaderapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt


// ---------------- PdfRenderer Manager ----------------
class PdfRendererManager(file: File) {
    private val fileDescriptor: ParcelFileDescriptor =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

    private val pdfRenderer = PdfRenderer(fileDescriptor)
    private val cache = LruCache<String, Bitmap>(50)
    private val renderMutex = Mutex()

    val pageCount: Int
        get() = pdfRenderer.pageCount

    suspend fun renderPageToWidth(index: Int, targetWidthPx: Int): Bitmap {
        val safeWidth = targetWidthPx.coerceAtLeast(1)
        val key = "$index@$safeWidth"

        cache.get(key)?.let { return it }

        return withContext(Dispatchers.IO) {
            renderMutex.withLock {
                pdfRenderer.openPage(index).use { page ->
                    val scale = safeWidth.toFloat() / page.width.toFloat()
                    val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)

                    val bitmap = Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    cache.put(key, bitmap)
                    bitmap
                }
            }
        }
    }

    fun close() {
        cache.evictAll()
        pdfRenderer.close()
        fileDescriptor.close()
    }
}

// ---------------- Main PDF Screen ----------------
@Composable
fun PdfViewerScreen(
    pdfUrl: String,
    bookId: String,
    pageSpacing: Dp = 12.dp,
    pageCornerRadius: Dp = 10.dp,
) {
    val context = LocalContext.current
    var file by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pdfUrl) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                file = downloadPdfFile(context, pdfUrl)
                error = if (file == null) "Failed to download PDF" else null
            } catch (e: Exception) {
                error = e.message ?: "Error loading PDF"
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> LoadingView()
        error != null -> ErrorView(error!!)
        file != null -> {
            val manager = remember { PdfRendererManager(file!!) }
            DisposableEffect(Unit) { onDispose { manager.close() } }

            PdfReaderScaffold(
                manager = manager,
                bookId = bookId,
                pageSpacing = pageSpacing,
                pageCornerRadius = pageCornerRadius
            )
        }
        else -> ErrorView("No PDF file found")
    }
}

// ---------------- Scaffold ----------------
@Composable
fun PdfReaderScaffold(
    manager: PdfRendererManager,
    bookId: String,
    pageSpacing: Dp,
    pageCornerRadius: Dp,
) {
    val context = LocalContext.current
    val pageCount = remember { manager.pageCount }
    val savedIndex = remember { getLastReadPage(context, bookId).coerceIn(0, pageCount - 1) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = savedIndex)

    var scale by remember { mutableFloatStateOf(1.2f) }

    ContinuousPdfReader(
        manager = manager,
        bookId = bookId,
        pageSpacing = pageSpacing,
        pageCornerRadius = pageCornerRadius,
        listState = listState,
        scale = scale,
        onScaleChange = { scale = it },
        onToggleUi = { }
    )
}

// ---------------- Continuous Reader ----------------
@Composable
fun ContinuousPdfReader(
    manager: PdfRendererManager,
    bookId: String,
    pageSpacing: Dp,
    pageCornerRadius: Dp,
    listState: LazyListState,
    scale: Float,
    onScaleChange: (Float) -> Unit,
    onToggleUi: () -> Unit
) {
    val context = LocalContext.current
    val pageCount = remember { manager.pageCount }
    var offsetX by remember { mutableStateOf(0f) }

    val doubleTapGesture = Modifier.pointerInput(scale) {
        detectTapGestures(
            onTap = { onToggleUi() },
            onDoubleTap = {
                val newScale = if (scale > 1f) 1f else 2f
                onScaleChange(newScale)
            }
        )
    }

    val pinchZoomGesture = Modifier.pointerInput(scale) {
        detectTransformGestures { _, _, zoom, _ ->
            val newScale = (scale * zoom).coerceIn(1f, 5f)
            onScaleChange(newScale)
        }
    }

    // Save last read page
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index -> saveLastReadPage(context, bookId, index) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F7))
            .then(doubleTapGesture)
            .then(pinchZoomGesture)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(pageSpacing),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(count = pageCount, key = { it }) { pageIndex ->
                PdfPageCard(
                    manager = manager,
                    pageIndex = pageIndex,
                    cornerRadius = pageCornerRadius,
                    scale = scale,
                    offsetX = offsetX,
                    onOffsetChange = { offsetX = it }
                )
            }
        }
    }
}

// ---------------- PDF Page Card ----------------
@Composable
fun PdfPageCard(
    manager: PdfRendererManager,
    pageIndex: Int,
    cornerRadius: Dp,
    scale: Float,
    offsetX: Float,
    onOffsetChange: (Float) -> Unit
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var containerWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(pageIndex, containerWidthPx, scale) {
        if (containerWidthPx > 0) {
            bitmap = manager.renderPageToWidth(pageIndex, (containerWidthPx * scale).toInt())
        }
    }

    val baseHeightPx = remember(bitmap, scale) {
        val b = bitmap
        if (b != null && scale > 0f) (b.height / scale).toInt() else 0
    }
    val scaledHeightDp = with(density) { (baseHeightPx * scale).toDp() }

    val maxTranslateXPx = remember(containerWidthPx, scale) {
        val scaledWidth = containerWidthPx * scale
        val extra = (scaledWidth - containerWidthPx).coerceAtLeast(0f)
        extra / 2f
    }

    LaunchedEffect(scale, containerWidthPx) {
        onOffsetChange(
            if (scale <= 1f || containerWidthPx == 0) 0f
            else offsetX.coerceIn(-maxTranslateXPx, maxTranslateXPx)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                containerWidthPx = coordinates.size.width
            }
            .height(scaledHeightDp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (scale > 1f) {
                            val newX =
                                (offsetX + delta).coerceIn(-maxTranslateXPx, maxTranslateXPx)
                            onOffsetChange(newX)
                        }
                    },
                    enabled = scale > 1f
                )
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX
                ),
            shape = RoundedCornerShape(cornerRadius),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            bitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Page $pageIndex",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEFEFC)),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

// ---------------- Helpers ----------------
private fun saveLastReadPage(context: Context, bookId: String, pageIndex: Int) {
    context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
        .edit { putInt("last_page_$bookId", pageIndex) }
}

private fun getLastReadPage(context: Context, bookId: String): Int {
    return context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
        .getInt("last_page_$bookId", 0)
}

private suspend fun downloadPdfFile(context: Context, url: String): File? =
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null

                val file = File.createTempFile("temp_pdf_", ".pdf", context.cacheDir)
                response.body?.byteStream()?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                file
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

@Composable
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}
