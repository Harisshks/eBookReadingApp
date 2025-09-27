package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.ui.components.BookCard
import com.example.bookreaderapp.ui.components.ProfileDialog
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.AuthViewModel
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.GoogleAuthUiClient
import com.example.bookreaderapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GenreBooksScreen(
    navController: NavController,
    genre: String,
    booksViewModel: BooksViewModel,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val books by booksViewModel.books.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    val profile by profileViewModel.profile.collectAsState()


    val filteredBooks = books.filter {
        it.genre == genre &&
                (it.title.contains(searchQuery, ignoreCase = true) || it.author.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        SearchAndProfileBar(
            profile = profile,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { showProfileDialog = true }
        )

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
            items(filteredBooks) { book ->
                BookCard(book = book, onClick = {
                    navController.navigate("book_details/${book.id}")
                })
            }
        }
    }

    // Profile Dialog
    if (showProfileDialog) {
        val profile = profileViewModel.profile.collectAsState().value
        ProfileDialog(
            profile = profile,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                authViewModel.logout()

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
