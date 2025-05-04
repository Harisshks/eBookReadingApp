//package com.example.bookreaderapp.ui.screens
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.bookreaderapp.data.models.Book
//import com.example.bookreaderapp.viewmodel.BooksViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GenreBooksScreen(
//    genre: String,
//    booksViewModel: BooksViewModel,
//    navController: NavController
//) {
//    val booksInGenreState = booksViewModel.getBooksByGenre(genre).collectAsState(initial = emptyList())
//    val booksInGenre = booksInGenreState.value
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(text = genre) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        if (booksInGenre.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No books found in $genre", style = MaterialTheme.typography.bodyLarge)
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(booksInGenre) { book->
//                    BookCard(book = book, onClick = {
//                        navController.navigate("book_detail/${book.title}/${book.id}")
//                    })
//                }
//
//            }
//        }
//    }
//}
