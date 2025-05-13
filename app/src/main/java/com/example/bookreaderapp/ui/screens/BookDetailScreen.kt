package com.example.bookreaderapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.viewmodel.BooksViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore


@Composable
fun BookDetailContent(
    book: Book,
    lastReadPage: Int,
    navController: NavController,
    allBooks: List<Book>
) {
    val relatedBooks = allBooks.filter {
        it.genre.equals(book.genre, true) && it.id != book.id
    }

    var isExpanded by remember { mutableStateOf(false) }

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
                model = book.coverURL,
                contentDescription = "${book.title} Cover",
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
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

        // Description
        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Show less" else "Show more"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Related Books
        if (relatedBooks.isNotEmpty()) {
            Text(
                text = "Related Books",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow {
                items(relatedBooks) { relatedBook ->
                    Column(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                navController.navigate("book_detail/${relatedBook.title}/${relatedBook.id}")
                            }
                    ) {
                        AsyncImage(
                            model = relatedBook.coverURL,
                            contentDescription = relatedBook.title,
                            modifier = Modifier
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = relatedBook.title,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

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
}


@Composable
fun BookDetailScreen(title: String, bookId: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)
    val lastReadPage = sharedPrefs.getInt("last_page_$bookId", -1)

    val viewModel = BooksViewModel()
    val book by viewModel.getBookById(bookId).collectAsState(initial = null)
    val allBooks by viewModel.books.collectAsState()

    book?.let {
        BookDetailContent(
            book = it,
            lastReadPage = lastReadPage,
            navController = navController,
            allBooks = allBooks
        )
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Preview(showBackground = true)
@Composable
fun BookDetailContentPreview() {
    val dummyBook = Book(
        id = "1",
        title = "Atomic Habits",
        author = "James Clear",
        pdfurl = "https://example.com/atomic.pdf",
        genre = "Self Help",
        description = "A practical guide to building good habits and breaking bad ones.",
        coverURL = "https://raw.githubusercontent.com/Harisshks/Bookpdffiles/main/ahpic.jpg"
    )

    BookDetailContent(
        book = dummyBook,
        lastReadPage = -1,
        navController = rememberNavController(),
        allBooks = emptyList()
    )
}

