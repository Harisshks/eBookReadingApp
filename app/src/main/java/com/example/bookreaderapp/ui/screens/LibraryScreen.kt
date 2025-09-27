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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: BooksViewModel,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
)
{
    val categories = listOf("Completed", "Reading", "Ongoing", "Unread", "Important")
    val pagerState = rememberPagerState { categories.size }
    val books = viewModel.library.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    val profile by profileViewModel.profile.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        //  Search + Profile
        SearchAndProfileBar(
            profile = profile,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { showProfileDialog = true },
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            navController.navigate("book_details/${book.id}")
                        }
                    )

                }
            }
        }
    }

    if (showProfileDialog) {
        val profile = googleAuthUiClient.getSignedInUser()

        ProfileDialog(
            profile = profile,
            onDismiss = { showProfileDialog = false },
            onLogout = {
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

@Composable
fun AddToLibraryDialog(
    currentCategory: String?,
    onCategoryChange: (String) -> Unit,
    onRemove: () -> Unit,   // <-- new
    onDismiss: () -> Unit
) {
    val categories = listOf("Completed", "Reading", "Ongoing", "Unread", "Important")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move to Library Category") },
        text = {
            Column {
                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (category != currentCategory) {
                                    onCategoryChange(category)
                                }
                                onDismiss()
                            }
                            .background(
                                if (category == currentCategory) Color(0xFF2196F3)
                                else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (category == currentCategory) Color.White else Color.Black,
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

                Spacer(modifier = Modifier.height(16.dp))

                // 🔴 Remove from Library option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRemove()   // <-- call ViewModel remove function
                            onDismiss()
                        }
                        .background(Color(0xFFFFCDD2), shape = RoundedCornerShape(8.dp)) // light red
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove from Library", color = Color.Red)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
