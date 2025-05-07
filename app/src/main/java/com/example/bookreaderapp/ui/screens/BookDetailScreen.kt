package com.example.bookreaderapp.ui.screens


import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.viewmodel.BooksViewModel


@Composable
fun BookDetailScreen(title: String, bookId: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
    val lastReadPage = sharedPrefs.getInt("last_page_$bookId", -1)

    val viewModel = BooksViewModel()
    val book by viewModel.getBookById(bookId).collectAsState(initial = null)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        book?.let {
            Text(text = "Author: ${it.author}")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("pdf_view/${Uri.encode(it.pdfurl)}/${it.id}")
            }) {
                Text("Read Now")
            }

            if (lastReadPage > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    navController.navigate("pdf_view/${Uri.encode(it.pdfurl)}/${it.id}")
                }) {
                    Text("Resume Reading (Page ${lastReadPage + 1})")
                }
            }

        } ?: CircularProgressIndicator()
    }
}

