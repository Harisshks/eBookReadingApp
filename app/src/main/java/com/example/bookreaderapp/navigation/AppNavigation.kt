// AppNavigation.kt
package com.example.bookreaderapp.navigation

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bookreaderapp.PdfViewerScreen
import com.example.bookreaderapp.ui.screens.*
import com.example.bookreaderapp.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val booksViewModel: BooksViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.Wishlist
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = user != null && bottomNavItems.any { it.route == currentRoute }

    // ✅ Decide startDestination only once based on auth state
    val startDestination = remember(user) {
        if (user == null) "welcome" else BottomNavItem.Home.route
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    elevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            selectedContentColor = Color(0xFF2196F3),
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 🆕 Welcome Screen
            composable("welcome") {
                WelcomeScreen(
                    onLoginClick = { navController.navigate("login") },
                    onSignupClick = { navController.navigate("signup") }
                )
            }

            // 🔐 Auth screens
            composable("login") {
                LoginScreen(
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient
                )
            }

            composable("signup") {
                SignupScreen(
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient
                )
            }

            // 🏠 Main app screens
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    navController,
                    booksViewModel,
                    profileViewModel,
                    googleAuthUiClient
                )
            }

            composable(BottomNavItem.Library.route) {
                LibraryScreen(
                    booksViewModel,
                    navController,
                    profileViewModel,
                    authViewModel,
                    googleAuthUiClient
                )
            }

            composable(BottomNavItem.Wishlist.route) {
                WishlistScreen(
                    booksViewModel,
                    navController,
                    profileViewModel,
                    authViewModel,
                    googleAuthUiClient
                )
            }

            composable("edit_profile") {
                EditProfileScreen(profileViewModel, navController)
            }

            composable("all_genres") {
                AllGenreScreen(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel,
                    googleAuthUiClient = googleAuthUiClient
                )
            }

            composable(
                "genre_books/{genre}",
                arguments = listOf(navArgument("genre") { type = NavType.StringType })
            ) { backStackEntry ->
                val genreEncoded = backStackEntry.arguments?.getString("genre") ?: ""
                val genre = Uri.decode(genreEncoded)
                GenreBooksScreen(
                    navController = navController,
                    genre = genre,
                    booksViewModel = booksViewModel,
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel,
                    googleAuthUiClient = googleAuthUiClient
                )
            }

            composable(
                "category_books/{categories}",
                arguments = listOf(navArgument("categories") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("categories") ?: ""
                CategoryBooksScreen(
                    navController = navController,
                    categories = category,
                    booksViewModel = booksViewModel
                )
            }

            // 📚 Book Details Screen
            composable(
                "book_details/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val allBooks = booksViewModel.books.collectAsState().value
                val selectedBook = allBooks.find { it.id == bookId }

                selectedBook?.let { book ->
                    BookDetailScreen(
                        book = book,
                        lastReadPage = 0,
                        navController = navController,
                        allBooks = allBooks,
                        booksViewModel = booksViewModel
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 📖 PDF Viewer Screen
            composable(
                "pdf_view/{pdfUrlEncoded}/{bookId}",
                arguments = listOf(
                    navArgument("pdfUrlEncoded") { type = NavType.StringType },
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val pdfUrl = Uri.decode(backStackEntry.arguments?.getString("pdfUrlEncoded") ?: "")
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                PdfViewerScreen(pdfUrl = pdfUrl, bookId = bookId)
            }
        }
    }
}
