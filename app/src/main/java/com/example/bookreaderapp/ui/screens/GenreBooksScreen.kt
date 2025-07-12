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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.ui.components.BookCard
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.ProfileViewModel

@Composable
fun GenreBooksScreen(
    navController: NavController,
    genre: String,
    booksViewModel: BooksViewModel,
    profileViewModel: ProfileViewModel
) {
    val books by booksViewModel.books.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val profile = profileViewModel.profile.collectAsState().value
    val initial = profile?.name?.firstOrNull()?.uppercase() ?: "H"


    val genreBooks = books.filter {
        it.genre == genre &&
                (it.title.contains(searchQuery, ignoreCase = true) || it.author.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // 🔍 Search + Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search books...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
                    .clickable { navController.navigate("profile") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = initial, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Genre: $genre",
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(genreBooks) { book ->
                BookCard(book = book,onClick= {
                    navController.navigate("book_detail/${book.title}/${book.id}")
                })
            }
        }
    }
}
