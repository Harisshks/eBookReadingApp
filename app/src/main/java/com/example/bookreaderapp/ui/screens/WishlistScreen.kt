package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    booksViewModel: BooksViewModel = viewModel(),
    navController: NavController,
    profileViewModel : ProfileViewModel
) {
    val wishlistBooks = booksViewModel.wishlist.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = wishlistBooks.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        SearchAndProfileBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = {
                navController.navigate("profile")
            },
            profileViewModel
        )

        if (filteredBooks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your wishlist is empty", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBooks) { book ->
                    WishlistBookCard(book = book) {
                        navController.navigate("book_detail/${book.title}/${book.id}")
                    }
                }
            }
        }
    }
}


@Composable
fun WishlistBookCard(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = book.coverurl,
            contentDescription = "Book Cover",
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(vertical = 6.dp, horizontal = 8.dp)
        ) {
            Text(
                text = book.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}
