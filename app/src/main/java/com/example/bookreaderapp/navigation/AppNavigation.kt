package com.example.bookreaderapp.navigation


import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgument
import com.example.bookreaderapp.PdfViewerScreen
import com.example.bookreaderapp.ui.screens.BookDetailScreen
import com.example.bookreaderapp.ui.screens.EditProfileScreen
import com.example.bookreaderapp.ui.screens.GenreBooksScreen
import com.example.bookreaderapp.ui.screens.HomeScreen
import com.example.bookreaderapp.ui.screens.LibraryScreen
import com.example.bookreaderapp.ui.screens.LoginScreen
import com.example.bookreaderapp.ui.screens.ProfileScreen
import com.example.bookreaderapp.ui.screens.SettingsScreen
import com.example.bookreaderapp.ui.screens.SignupScreen
import com.example.bookreaderapp.ui.screens.WishlistScreen
import com.example.bookreaderapp.viewmodel.AuthViewModel
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.ProfileViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val booksViewModel: BooksViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel : ProfileViewModel = viewModel()
    val user by authViewModel.currentUser.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    // Set start destination based on authentication state
    LaunchedEffect(user) {
        startDestination = if (user == null) "login" else BottomNavItem.Home.route
    }

    if (startDestination == null) {
        // Show loading screen while determining start destination
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        bottomBar = {
            if (user != null) {
                BottomNavigation(
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    elevation = 8.dp
                ) {
                    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
                    val bottomNavItems = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Library,
                        BottomNavItem.Wishlist,
                        BottomNavItem.Settings
                    )

                    bottomNavItems.forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.route == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            selectedContentColor = Color(0xFFFF9800),
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login
            composable("login") {
                LoginScreen(navController = navController, authViewModel = authViewModel)
            }

            // Signup
            composable("signup") {
                val profileViewModel: ProfileViewModel = viewModel()
                SignupScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel
                )
            }


            // Home
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController, booksViewModel = booksViewModel, profileViewModel = profileViewModel)
            }
            composable("profile") {
                ProfileScreen(
                    profileViewModel = viewModel(),
                    onEditProfile = { navController.navigate("edit_profile") },
                    onLogout = {
                        profileViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    profileViewModel = viewModel(),
                    navController = navController
                )
            }
            composable("genre_books/{genre}") {
                val genre = it.arguments?.getString("genre") ?: ""
                GenreBooksScreen(navController, genre, booksViewModel , profileViewModel)
            }

            // Library
            composable(BottomNavItem.Library.route) {
                LibraryScreen(booksViewModel,navController,profileViewModel)
            }

            // Wishlist
            composable(BottomNavItem.Wishlist.route) {
                WishlistScreen(booksViewModel = booksViewModel, navController = navController,profileViewModel)
            }

            // Settings
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }

            // Book Detail
            composable(
                "book_detail/{title}/{bookId}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val allBooksState = booksViewModel.books.collectAsState()
                val allBooks = allBooksState.value
                val selectedBook = allBooks.find { it.id == bookId }

                selectedBook?.let { book ->
                    val lastReadPage = 0 // Replace with real value
                    BookDetailScreen(
                        book = book,
                        lastReadPage = lastReadPage,
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

            // PDF Viewer
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
