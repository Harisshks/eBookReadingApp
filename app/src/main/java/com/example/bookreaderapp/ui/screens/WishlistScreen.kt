package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    booksViewModel: BooksViewModel = viewModel(),
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val wishlistBooks = booksViewModel.wishlist.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }

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
            profile = profileViewModel.profile.collectAsState().value,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { showProfileDialog = true },
        )
        Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxSize().padding(top = 16.dp)
            ) {
                items(filteredBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            navController.navigate("book_details/${book.title}/${book.id}")
                        }
                    )

                }
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


