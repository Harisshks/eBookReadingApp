//package com.example.bookreaderapp.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.example.bookreaderapp.viewmodel.ProfileViewModel
//
//@Composable
//fun ProfileScreen(
//    profileViewModel: ProfileViewModel,
//    onEditProfile: () -> Unit = {},
//    onLogout: () -> Unit = {}
//) {
//    LaunchedEffect(Unit) {
//        profileViewModel.fetchUserProfile()
//    }
//
//    val profile by profileViewModel.profile.collectAsState()
//    val isLoading by profileViewModel.isLoading.collectAsState()
//
//    if (isLoading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//    } else {
//        if (profile == null) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("No profile data found.")
//            }
//        } else {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White)
//                    .padding(16.dp)
//            ) {
//                // 👤 Profile Card
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 12.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        if (!profile?.profileImageUrl.isNullOrEmpty()) {
//                            AsyncImage(
//                                model = profile?.profileImageUrl,
//                                contentDescription = "Profile Picture",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.LightGray)
//                            )
//                        } else {
//                            Box(
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.Gray),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = profile?.name?.firstOrNull()?.uppercase() ?: "U",
//                                    color = Color.White,
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.width(12.dp))
//
//                        Column {
//                            Text(
//                                text = profile?.name ?: "Reader",
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(
//                                text = profile?.email ?: "",
//                                fontSize = 14.sp,
//                                color = Color.Gray
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.weight(1f))
//
//                        IconButton(onClick = onEditProfile) {
//                            Icon(
//                                imageVector = Icons.Default.Edit,
//                                contentDescription = "Edit Profile",
//                                tint = Color.Gray
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Button(
//                    onClick = onLogout,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    shape = RoundedCornerShape(10.dp)
//                ) {
//                    Text("Logout", color = Color.White)
//                }
//            }
//        }
//    }
//}

package com.example.bookreaderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookreaderapp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val profile by profileViewModel.profile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        profile == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No profile data found.", fontSize = 16.sp)
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {

                // Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!profile?.profileImageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = profile?.profileImageUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile?.name?.firstOrNull()?.uppercase() ?: "U",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = profile?.name ?: "Reader",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile?.email ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = onEditProfile) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                // Reading Stats
                Text(
                    text = "📚 Reading Stats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Books Read", value = profile?.booksRead ?: 0)
                    StatItem(label = "In Progress", value = profile?.booksInProgress ?: 0)
                    StatItem(label = "Favorites", value = profile?.favoritesCount ?: 0)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Logout", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
