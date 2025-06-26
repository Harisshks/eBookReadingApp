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
import androidx.compose.runtime.collectAsState
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
import com.example.bookreaderapp.ui.screens.HomeScreen
import com.example.bookreaderapp.ui.screens.LibraryScreen
import com.example.bookreaderapp.ui.screens.SettingsScreen
import com.example.bookreaderapp.ui.screens.WishlistScreen
import com.example.bookreaderapp.viewmodel.BooksViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val booksViewModel: BooksViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.Wishlist,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.Black,
                contentColor = Color.White,
                elevation = 8.dp
            ) {
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination
                bottomNavItems.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
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
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController, booksViewModel = booksViewModel)
            }

            // Library
            composable(BottomNavItem.Library.route) {
                LibraryScreen()
            }

            // Wishlist — with access to ViewModel + navController
            composable(BottomNavItem.Wishlist.route) {
                WishlistScreen(
                    booksViewModel = booksViewModel,
                    navController = navController
                )
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
                    val lastReadPage = 0 // Replace with actual last-read page logic
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
