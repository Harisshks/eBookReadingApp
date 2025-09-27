package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.R
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.ui.components.BookCard
import com.example.bookreaderapp.ui.components.ProfileDialog
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.GoogleAuthUiClient
import com.example.bookreaderapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    booksViewModel: BooksViewModel,
    profileViewModel: ProfileViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val books by booksViewModel.books.collectAsState()
    val allGenresImage = painterResource(id = R.drawable.allgenre)
    val profile by profileViewModel.profile.collectAsState()


    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }


    val filteredBooks = remember(searchQuery, books) {
        books.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true)
        }
    }

    val topCategories = remember {
        listOf("Top Collections", "Start Here", "Trending Now")
    }
    val bottomCategories = remember {
        listOf("Only on BookHive", "Most Awaited", "Popular in Children")
    }

    val booksByCategory = remember(filteredBooks) {
        (topCategories + bottomCategories).associateWith { category ->
            filteredBooks.filter { it.categories.contains(category) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top search/profile bar
        SearchAndProfileBar(
            profile = profile,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { showProfileDialog = true },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                var renderedTop = false

                topCategories.forEach { category ->
                    val categoryBooks = booksByCategory[category] ?: emptyList()
                    if (categoryBooks.isNotEmpty()) {
                        renderedTop = true
                        item("header_$category") {
                            CategoryHeader(category) {
                                navController.navigate("category_books/$category")
                            }
                        }
                        item("books_$category") {
                            BooksRow(categoryBooks) { book ->
                                navController.navigate("book_details/${book.id}")
                            }
                        }
                    }
                }

                if (renderedTop) {
                    item("all_categories_card") {
                        AllCategoriesCard(navController, allGenresImage)
                    }
                }

                bottomCategories.forEach { category ->
                    val categoryBooks = booksByCategory[category] ?: emptyList()
                    if (categoryBooks.isNotEmpty()) {
                        item("header_$category") {
                            CategoryHeader(category) {
                                navController.navigate("category_books/$category")
                            }
                        }
                        item("books_$category") {
                            BooksRow(categoryBooks) { book ->
                                navController.navigate("book_details/${book.id}")
                            }
                        }
                    }
                }

                if (!renderedTop) {
                    item("all_categories_fallback") {
                        AllCategoriesCard(navController, allGenresImage)
                    }
                }
            }
        } else {
            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBooks, key = { it.id }) { book ->
                        BookCard(book = book, onClick = {
                            navController.navigate("book_details/${book.id}")
                        })
                    }
                }
            }
        }
    }


    if (showProfileDialog) {
        val profile = googleAuthUiClient.getSignedInUser() // directly from Firebase Google user

        ProfileDialog(
            profile = profile,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                CoroutineScope(Dispatchers.Main).launch {
                    googleAuthUiClient.signOut()
                }

                navController.navigate("login") {
                    popUpTo("all_genres") { inclusive = true }
                }
                showProfileDialog = false
            },
            onLibraryClick = {
                navController.navigate("library") {
                    launchSingleTop = true
                }
                showProfileDialog = false
            },
            googleAuthUiClient = googleAuthUiClient
        )
    }


}

@Composable
private fun AllCategoriesCard(navController: NavController, allGenresImage: androidx.compose.ui.graphics.painter.Painter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable { navController.navigate("all_genres") },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
//                Image(
//                    painter = allGenresImage,
//                    contentDescription = "Categories Background",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Text(
                    text = "All Categories",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "See All",
            color = Color(0xFF03A9F4),
            modifier = Modifier.clickable { onSeeAllClick()}
        )
    }
}

@Composable
private fun BooksRow(books: List<Book>, onBookClick: (Book) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(book = book, onClick = { onBookClick(book) })
        }
    }
}
