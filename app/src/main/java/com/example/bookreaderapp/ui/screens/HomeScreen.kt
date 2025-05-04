package com.example.bookreaderapp.ui.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.viewmodel.BooksViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.example.bookreaderapp.data.models.Book



@Composable
fun HomeScreen(navController: NavController, booksViewModel: BooksViewModel) {
    val books by booksViewModel.books.collectAsState()

    // Group books by genre
    val booksByGenre = books.groupBy { it.genre }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        booksByGenre.forEach { (genre, genreBooks) ->
            item {
                Text(
                    text = genre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(genreBooks) { book ->
                        BookCard(book = book, onClick = {
                            navController.navigate("book_detail/${book.title}/${book.id}")
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun GenreChip(genre: String) {
    AssistChip(
        onClick = { /* Handle genre click */ },
        modifier = Modifier.padding(4.dp),
        label = { Text(genre) }
    )
}

// BookCard.kt
@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook, // or use a real image
                contentDescription = "Book Icon",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


