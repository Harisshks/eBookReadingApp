//package com.example.bookreaderapp.ui.screens
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Divider
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.bookreaderapp.data.models.Book
//import com.example.bookreaderapp.ui.components.BookCard
//import com.example.bookreaderapp.ui.components.SearchAndProfileBar
//import com.example.bookreaderapp.viewmodel.BooksViewModel
//import com.example.bookreaderapp.viewmodel.ProfileViewModel
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun HomeScreen(
//    navController: NavController,
//    booksViewModel: BooksViewModel,
//    profileViewModel: ProfileViewModel
//) {
//    val books by booksViewModel.books.collectAsState()
//    var searchQuery by remember { mutableStateOf("") }
//    var selectedGenre by remember { mutableStateOf<String?>(null) }
//
//    var showLibraryDialog by remember { mutableStateOf(false) }
//    var selectedBookForLibrary by remember { mutableStateOf<Book?>(null) }
//    val libraryBooks by booksViewModel.library.collectAsState()
//    val currentCategory = libraryBooks.find { it.id == selectedBookForLibrary?.id }?.category
//
//
//    val filteredBooks = books.filter {
//        it.title.contains(searchQuery, ignoreCase = true) ||
//                it.author.contains(searchQuery, ignoreCase = true)
//    }
//
//    val genreFilteredBooks = selectedGenre?.let {
//        filteredBooks.filter { it.genre == selectedGenre }
//    } ?: filteredBooks
//
//    val booksByGenre = filteredBooks.groupBy { it.genre }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .padding(16.dp)
//    ) {
//        //  Search + Profile Row
//        SearchAndProfileBar(
//            searchQuery = searchQuery,
//            onSearchQueryChange = { searchQuery = it },
//            onProfileClick = {
//                navController.navigate("profile")
//            },
//            profileViewModel = profileViewModel
//        )
//
//        //  Genre Chips
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            modifier = Modifier.padding(vertical = 8.dp)
//        ) {
//            val allGenres = books.map { it.genre }.distinct()
//            items(allGenres) { genre ->
//                val isSelected = selectedGenre == genre
//                Box(
//                    modifier = Modifier
//                        .background(
//                            if (isSelected) Color(0xFF2196F3) else Color.DarkGray,
//                            RoundedCornerShape(16.dp)
//                        )
//                        .clickable {
//                            selectedGenre = if (isSelected) null else genre
//                        }
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                ) {
//                    Text(text = genre, color = Color.White)
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        if (selectedGenre != null) {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(genreFilteredBooks) { book ->
//                    BookCard(
//                        book = book,
//                        onClick = {
//                            navController.navigate("book_detail/${book.title}/${book.id}")
//                        },
//                        onLongClick = {
//                            selectedBookForLibrary = book
//                            showLibraryDialog = true
//                        }
//                    )
//                }
//            }
//        } else {
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                booksByGenre.forEach { (genre, genreBooks) ->
//                    item {
//                        Spacer(modifier = Modifier.height(12.dp))
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = genre,
//                                color = Color.White,
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                            Text(
//                                text = "See All",
//                                color = Color(0xFF2196F3),
//                                modifier = Modifier.clickable {
//                                    navController.navigate("genre_books/$genre")
//                                }
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//
//                    item {
//                        LazyRow(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            items(genreBooks) { book ->
//                                BookCard(
//                                    book = book,
//                                    onClick = {
//                                        navController.navigate("book_detail/${book.title}/${book.id}")
//                                    },
//                                    onLongClick = {
//                                        selectedBookForLibrary = book
//                                        showLibraryDialog = true
//                                    }
//                                )
//                            }
//                        }
//                    }
//
//                    item {
//                        HorizontalDivider(
//                            modifier = Modifier.padding(vertical = 16.dp),
//                            thickness = 1.dp,
//                            color = Color.DarkGray
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    // 📚 Library Dialog
//    if (showLibraryDialog && selectedBookForLibrary != null) {
//        AddToLibraryDialog(
//            currentCategory = currentCategory,
//            onSelectCategory = { category ->
//                booksViewModel.addToLibrary(selectedBookForLibrary!!, category)
//                showLibraryDialog = false
//            },
//            onDismiss = { showLibraryDialog = false }
//        )
//
//    }
//    if (showLibraryDialog && selectedBookForLibrary != null) {
//        AddToLibraryDialog(
//            currentCategory = currentCategory,
//            onSelectCategory = { category ->
//                booksViewModel.toggleLibrary(selectedBookForLibrary!!, category)
//                showLibraryDialog = false
//            },
//            onDismiss = {
//                showLibraryDialog = false
//            }
//        )
//    }
//
//}

package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.ui.components.BookCard
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.ProfileViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    booksViewModel: BooksViewModel,
    profileViewModel: ProfileViewModel
) {
    val books by booksViewModel.books.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    var showLibraryDialog by remember { mutableStateOf(false) }
    var selectedBookForLibrary by remember { mutableStateOf<Book?>(null) }
    val libraryBooks by booksViewModel.library.collectAsState()
    val currentCategory = libraryBooks.find { it.id == selectedBookForLibrary?.id }?.category

    val filteredBooks = books.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
    }

    val genreFilteredBooks = selectedGenre?.let {
        filteredBooks.filter { it.genre == selectedGenre }
    } ?: filteredBooks

    val booksByGenre = filteredBooks.groupBy { it.genre }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        SearchAndProfileBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = {
                navController.navigate("profile")
            },
            profileViewModel = profileViewModel
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            val allGenres = books.map { it.genre }.distinct()
            items(allGenres) { genre ->
                val isSelected = selectedGenre == genre
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) Color(0xFFFF9800) else Color(0xFF2C2C2E))
                        .clickable {
                            selectedGenre = if (isSelected) null else genre
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.Black else Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedGenre != null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(genreFilteredBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            navController.navigate("book_detail/${book.title}/${book.id}")
                        },
                        onLongClick = {
                            selectedBookForLibrary = book
                            showLibraryDialog = true
                        }
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                booksByGenre.forEach { (genre, genreBooks) ->
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = genre,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "See All",
                                color = Color(0xFF03A9F4),
                                modifier = Modifier.clickable {
                                    navController.navigate("genre_books/$genre")
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(genreBooks) { book ->
                                BookCard(
                                    book = book,
                                    onClick = {
                                        navController.navigate("book_detail/${book.title}/${book.id}")
                                    },
                                    onLongClick = {
                                        selectedBookForLibrary = book
                                        showLibraryDialog = true
                                    }
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }

    if (showLibraryDialog && selectedBookForLibrary != null) {
        AddToLibraryDialog(
            currentCategory = currentCategory,
            onSelectCategory = { category ->
                booksViewModel.toggleLibrary(selectedBookForLibrary!!, category)
                showLibraryDialog = false
            },
            onDismiss = {
                showLibraryDialog = false
            }
        )
    }
}
