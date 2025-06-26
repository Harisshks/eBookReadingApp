package com.example.bookreaderapp.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.viewmodel.BooksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book,
    lastReadPage: Int,
    navController: NavController,
    allBooks: List<Book>,
    booksViewModel: BooksViewModel = viewModel()

) {
    val scrollState = rememberScrollState()
    val similarBooks = allBooks.filter { it.genre == book.genre && it.id != book.id }
    val authorBooks = allBooks.filter { it.author == book.author && it.id != book.id }
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val isWishlisted = booksViewModel.wishlist.collectAsState().value.any { it.id == book.id }
    val coroutineScope = rememberCoroutineScope()




    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black)
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }

                IconButton(onClick = {
                    Toast.makeText(context, "Adding to Wishlist...", Toast.LENGTH_SHORT).show()
                    booksViewModel.toggleWishlist(book)
                    coroutineScope.launch {
                        delay(1000) // Wait 1.5 seconds
                        Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (isWishlisted) Color.Red else Color.White
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                menuExpanded = false
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, book.pdfurl)
                                    type = "text/plain"
                                }
                                val chooser = Intent.createChooser(shareIntent, "Share PDF Link")
                                context.startActivity(chooser)
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

    // Header Section
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = book.coverurl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {

                Text(book.title, style = MaterialTheme.typography.titleLarge.copy(color = Color.White))
                Text("By ${book.author}", color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(4.dp))

                Spacer(modifier = Modifier.height(12.dp))

                // Read Now Button
                Button(
                    onClick = {
                        navController.navigate("pdf_view/${Uri.encode(book.pdfurl)}/${book.id}")
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                ) {
                    Text(
                        text = if (lastReadPage > 0) "Continue Reading (Page ${lastReadPage + 1})" else "Read Now"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // eBook + Pages Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "eBook",
                            tint = Color.Gray
                        )
                        Text("eBook", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = book.pages.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Text("Pages", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

        }
        // Below your Read Now button
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))

        // About this book
        var isExpanded by remember { mutableStateOf(false) }

        Text("About this eBook", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            book.description,
            color = Color.LightGray,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
        ) {
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = if(isExpanded) "Read less" else "Read more",
                color = Color(0xFF90CAF9)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Author's Other Books
        if (authorBooks.isNotEmpty()) {
            Text("More by ${book.author}", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            HorizontalBookList(books = authorBooks, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Similar Genre Books
        if (similarBooks.isNotEmpty()) {
            Text("Similar eBooks", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            HorizontalBookList(books = similarBooks, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Details
        Text("eBook Details", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
        Spacer(modifier = Modifier.height(8.dp))

       // BookDetailRow("Language", book.language ?: "Unknown")
       // BookDetailRow("Features", book.features ?: "Digital book")
        BookDetailRow("Author", book.author)
        //BookDetailRow("Publisher", book.publisher ?: "Unknown")
       // BookDetailRow("Published on", book.releaseDate ?: "Unknown")
       // BookDetailRow("Pages", book.pageCount?.toString() ?: "N/A")
        BookDetailRow("Genres", book.genre)
        //BookDetailRow("DRM", if (book.isDRMFree) "This content is DRM free." else "DRM protected")
    }
}

@Composable
fun HorizontalBookList(books: List<Book>, navController: NavController) {
    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
        items(books) { book ->
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { navController.navigate("book_detail/${book.title}/${book.id}") }
            ) {
                AsyncImage(
                    model = book.coverurl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .size(120.dp, 160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    book.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Composable
fun BookDetailRow(label: String, value: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        Text(label, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, modifier = Modifier.weight(2f))
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailScreenPreview() {
    val dummyBook = Book(
        id = "1",
        title = "Atomic Habits",
        author = "James Clear",
        pdfurl = "https://example.com/atomic.pdf",
        genre = "Self Help",
        description = "A practical guide to building good habits and breaking bad ones.",
        coverurl = "https://raw.githubusercontent.com/Harisshks/Bookpdffiles/main/ahpic.jpg"
    )

    val relatedBooks = listOf(
        dummyBook.copy(id = "2", title = "The Power of Habit"),
        dummyBook.copy(id = "3", title = "Deep Work"),
        dummyBook.copy(id = "4", title = "Make Time")
    )

    BookDetailScreen(
        book = dummyBook,
        lastReadPage = 0,
        navController = rememberNavController(),
        allBooks = relatedBooks
    )
}
