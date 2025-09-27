package com.example.bookreaderapp.ui.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.viewmodel.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBooksScreen(
    navController: NavController,
    categories: String,
    booksViewModel: BooksViewModel
) {
    val allBooks by booksViewModel.books.collectAsState()
    val libraryBooks by booksViewModel.library.collectAsState()

    var showLibraryDialog by remember { mutableStateOf(false) }
    var selectedBookForLibrary by remember { mutableStateOf<Book?>(null) }

    val filteredBooks = remember(categories, allBooks) {
        allBooks.filter { it.categories.contains(categories) }
    }

    // ✅ currentCategory should be stateful so recompositions work
    var currentCategory by remember { mutableStateOf<String?>(null) }

    // Whenever a book is selected, fetch its category from library
    LaunchedEffect(selectedBookForLibrary, libraryBooks) {
        currentCategory = libraryBooks.find { it.id == selectedBookForLibrary?.id }?.category
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = categories,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black,
                titleContentColor = Color.White
            ),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredBooks) { book ->
                BookItem(
                    book = book,
                    booksViewModel = booksViewModel,
                    isWishlisted = booksViewModel.isInWishlist(book),
                    onWishlistToggle = { booksViewModel.toggleWishlist(book) },
                    onLibraryClick = {
                        selectedBookForLibrary = book
                        showLibraryDialog = true
                    },
                    onBookClick = {
                        navController.navigate("book_details/${book.id}")
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.8.dp,
                    color = Color.DarkGray
                )
            }
        }
    }

    if (showLibraryDialog && selectedBookForLibrary != null) {
        AddToLibraryDialog(
            currentCategory = currentCategory,
            onCategoryChange = { category ->
                val book = selectedBookForLibrary!!
                booksViewModel.toggleLibrary(book, category)
                currentCategory = category
                showLibraryDialog = false
            },
            onRemove = {
                val book = selectedBookForLibrary!!
                booksViewModel.removeFromLibrary(book.id)
                showLibraryDialog = false
            },
            onDismiss = {
                showLibraryDialog = false
            }
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookItem(
    book: Book,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onLibraryClick: () -> Unit,
    booksViewModel: BooksViewModel,
    onBookClick: () -> Unit
) {
    var localWishlisted by remember { mutableStateOf(isWishlisted) }
    val isInLibrary by booksViewModel.library.collectAsState()
    val inLibrary = isInLibrary.any { it.id == book.id }

    val libraryTint by animateColorAsState(
        targetValue = if (inLibrary) Color(0xFF2196F3) else Color.Gray,
        animationSpec = tween(durationMillis = 300)
    )

    val wishlistTint by animateColorAsState(
        targetValue = if (localWishlisted) Color.Red else Color.Gray,
        animationSpec = tween(durationMillis = 300)
    )

    val heartScale by animateFloatAsState(
        targetValue = if (localWishlisted) 1.3f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "heartScale"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            // Book Cover
            Image(
                painter = rememberAsyncImagePainter(book.coverurl),
                contentDescription = book.title,
                modifier = Modifier
                    .width(110.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Book Details
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by ${book.author}",
                        color = Color(0xFFB0B0B0),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.genre,
                        color = Color(0xFF90CAF9),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Action Buttons Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Library Button
                    IconButton(onClick = onLibraryClick) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Add to Library",
                            tint = libraryTint
                        )
                    }

                    // Wishlist Button with pop animation
                    IconButton(onClick = {
                        localWishlisted = !localWishlisted
                        onWishlistToggle()
                    }) {
                        Icon(
                            imageVector = if (localWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = wishlistTint,
                            modifier = Modifier.graphicsLayer {
                                scaleX = heartScale
                                scaleY = heartScale
                            }
                        )
                    }
                }
            }
        }
    }
}
