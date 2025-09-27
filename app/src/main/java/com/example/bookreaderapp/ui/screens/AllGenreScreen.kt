package com.example.bookreaderapp.ui.screens



import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreaderapp.R
import com.example.bookreaderapp.data.models.UserProfile
import com.example.bookreaderapp.ui.components.ProfileDialog
import com.example.bookreaderapp.ui.components.SearchAndProfileBar
import com.example.bookreaderapp.viewmodel.AuthViewModel
import com.example.bookreaderapp.viewmodel.GoogleAuthUiClient
import com.example.bookreaderapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AllGenreScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
){
    var searchQuery by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    val profile by profileViewModel.profile.collectAsState()


    val genres = listOf(
        "Fiction" to R.drawable.fiction,
        "Fantasy" to R.drawable.fantasy,
        "Romance" to R.drawable.romance,
        "Mystery" to R.drawable.mystery,
        "Self Help" to R.drawable.selfhelp,
        "Adventure" to R.drawable.adventure,
        "Children" to R.drawable.children
        ).filter { it.first.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        SearchAndProfileBar(
            profile = profile,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { showProfileDialog = true },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "All Genres",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(genres) { (title, imageRes) ->
                GenreCard(title, imageRes) {
                    navController.navigate("genre_books/${Uri.encode(title)}")
                }
            }
        }
    }

    // Profile Dialog
    if (showProfileDialog) {
        val profile = profileViewModel.profile.collectAsState().value
        ProfileDialog(
            profile = profile,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                authViewModel.logout()

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
fun GenreCard(title: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

//        "Personal Development" to R.drawable.personal_dev,
//        "Economy & Business" to R.drawable.economy_business,
//       "Fiction" to R.drawable.fiction,
//        "Classics" to R.drawable.classics,
//        "Romance" to R.drawable.romance,
//        "Religion & Spirituality" to R.drawable.religion_spirituality,
//        "Biographies" to R.drawable.biographies,
//        "Children" to R.drawable.children,
//        "Crime" to R.drawable.crime,
//        "Thrillers" to R.drawable.thrillers