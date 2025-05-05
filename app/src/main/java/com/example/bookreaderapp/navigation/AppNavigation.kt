package com.example.bookreaderapp.navigation


import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bookreaderapp.PDFViewActivity
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
                booksViewModel = booksViewModel // ✅ Pass it here
            ) // Just passing navController for now
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

        // PDF View Route
        composable(
            route = "pdf_view/{pdfUrl}",
            arguments = listOf(
                navArgument("pdfUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pdfUrl = Uri.decode(backStackEntry.arguments?.getString("pdfUrl") ?: "")
            val bookId = backStackEntry.arguments?.getString("bookId")?:""
            val context = LocalContext.current

            // Launch the PDF view activity
            LaunchedEffect(Unit) {
                val intent = Intent(context, PDFViewActivity::class.java)
                intent.putExtra("pdfUrl", pdfUrl)
                intent.putExtra("bookId", bookId) // Unique ID for saving progress

                context.startActivity(intent)
            }
        }
    }
}
