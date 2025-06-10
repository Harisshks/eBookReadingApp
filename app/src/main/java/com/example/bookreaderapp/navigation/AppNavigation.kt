package com.example.bookreaderapp.navigation


import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.compose.material.*
import androidx.compose.ui.unit.dp

import androidx.navigation.navArgument
import com.example.bookreaderapp.PdfViewerScreen
import com.example.bookreaderapp.ui.screens.BookDetailScreen
import com.example.bookreaderapp.ui.screens.HomeScreen
import com.example.bookreaderapp.ui.screens.LibraryScreen
import com.example.bookreaderapp.ui.screens.SettingsScreen
import com.example.bookreaderapp.viewmodel.BooksViewModel


//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//    val booksViewModel: BooksViewModel = viewModel()
//
//    NavHost(navController = navController, startDestination = "home") {
//        // Home Screen Route
//        composable("home") {
//            HomeScreen(
//                navController = navController,
//                booksViewModel = booksViewModel
//            )
//        }
//
//        // Book Detail Screen Route
//        composable(
//            route = "book_detail/{title}/{bookId}",
//            arguments = listOf(
//                navArgument("title") { type = NavType.StringType },
//                navArgument("bookId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val title = backStackEntry.arguments?.getString("title") ?: ""
//            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
//            BookDetailScreen(title = title, bookId = bookId, navController = navController)
//        }
//
//        // PDF Viewer Route (Compose version)
//        composable(
//            route = "pdf_view/{pdfUrlEncoded}/{bookId}",
//            arguments = listOf(
//                navArgument("pdfUrlEncoded") { type = NavType.StringType },
//                navArgument("bookId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val pdfUrlEncoded = backStackEntry.arguments?.getString("pdfUrlEncoded") ?: ""
//            val pdfUrl = Uri.decode(pdfUrlEncoded)
//            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
//            PdfViewerScreen(pdfUrl = pdfUrl, bookId = bookId)
//        }
//    }
//}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val booksViewModel: BooksViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            BottomNavigation (
                backgroundColor = Color.Black,
                contentColor = Color.White,
                elevation = 8.dp
            ){
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
                        selectedContentColor = Color(0xFFFF9800), // 🟠 Orange for selected items
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
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController, booksViewModel = booksViewModel)
            }

            composable(BottomNavItem.Library.route) {
                LibraryScreen()
            }

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
                BookDetailScreen(title = title, bookId = bookId, navController = navController)
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
