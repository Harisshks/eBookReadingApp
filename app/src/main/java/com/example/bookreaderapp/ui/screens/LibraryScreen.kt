package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.BooksViewModel
import com.example.bookreaderapp.viewmodel.ProfileViewModel
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: BooksViewModel,
    navController: NavController,
    profileViewModel: ProfileViewModel)
{
    val categories = listOf("Completed", "Reading", "Ongoing", "Unread", "Important")
    val pagerState = rememberPagerState { categories.size }
    val books = viewModel.library.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        //  Search + Profile
        SearchAndProfileBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { navController.navigate("profile") },
            profileViewModel
        )

        //  Tab Row
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Black,
            edgePadding = 8.dp
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    text = {
                        Text(
                            text = category,
                            color = if (pagerState.currentPage == index) Color.White else Color.Gray,
                            fontWeight = Bold

                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSize = PageSize.Fill
        ) { page ->
            val currentCategory = categories[page]
            val filteredBooks = books.filter {
                it.category == currentCategory &&
                        (it.title.contains(searchQuery, ignoreCase = true) ||
                                it.author.contains(searchQuery, ignoreCase = true))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBooks) { book ->
                    LibraryBookCard(book = book) {
                        navController.navigate("book_detail/${book.title}/${book.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun AddToLibraryDialog(
    currentCategory: String?,
    onSelectCategory: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf("Completed", "Reading", "Ongoing", "Unread", "Important")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Library") },
        text = {
            Column {
                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCategory(category) }
                            .background(
                                if (category == currentCategory) Color(0xFF2196F3)
                                else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (category == currentCategory) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}


@Composable
fun LibraryBookCard(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = book.coverurl,
            contentDescription = "Book Cover",
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = book.title,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 2
        )
    }
}
