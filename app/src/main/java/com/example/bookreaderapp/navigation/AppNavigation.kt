package com.example.bookreaderapp.navigation


import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bookreaderapp.PdfViewerScreen
import com.example.bookreaderapp.ui.screens.BookDetailScreen
import com.example.bookreaderapp.ui.screens.HomeScreen
import com.example.bookreaderapp.viewmodel.BooksViewModel



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val booksViewModel: BooksViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        // Home Screen Route
        composable("home") {
            HomeScreen(
                navController = navController,
                booksViewModel = booksViewModel
            )
        }

        // Book Detail Screen Route
        composable(
            route = "book_detail/{title}/{bookId}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailScreen(title = title, bookId = bookId, navController = navController)
        }

        // PDF Viewer Route (Compose version)
        composable(
            route = "pdf_view/{pdfUrlEncoded}/{bookId}",
            arguments = listOf(
                navArgument("pdfUrlEncoded") { type = NavType.StringType },
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pdfUrlEncoded = backStackEntry.arguments?.getString("pdfUrlEncoded") ?: ""
            val pdfUrl = Uri.decode(pdfUrlEncoded)
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            PdfViewerScreen(pdfUrl = pdfUrl, bookId = bookId)
        }
    }
}
