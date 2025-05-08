package com.example.bookreaderapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookreaderapp.viewmodel.BooksViewModel

@Composable
fun BookDetailScreen(title: String, bookId: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
    val lastReadPage = sharedPrefs.getInt("last_page_$bookId", -1)

    val viewModel = BooksViewModel()
    val book by viewModel.getBookById(bookId).collectAsState(initial = null)

    book?.let { book ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "${book.title} Cover",
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By ${book.author}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            Button(
                onClick = {
                    navController.navigate("pdf_view/${Uri.encode(book.pdfurl)}/${book.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (lastReadPage > 0) "Continue Reading (Page ${lastReadPage + 1})"
                    else "Read Now"
                )
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
