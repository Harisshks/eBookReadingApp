package com.example.bookreaderapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookreaderapp.data.models.UserProfile
import com.example.bookreaderapp.viewmodel.GoogleAuthUiClient
import kotlinx.coroutines.launch

@Composable
fun ProfileDialog(
    profile: UserProfile?,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onLibraryClick: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient

) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header Row - Profile Image + Name + Email
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!profile?.profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profile.profileImageUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF64B5F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.email?.firstOrNull()?.uppercase() ?: "R",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = profile?.name ?: "Reader",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = profile?.email ?: "email@example.com",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                // Options
                ProfileDialogOption("Library", onLibraryClick)
                ProfileDialogOption("Help and Feedback")

                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                // Footer links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        "Privacy Policy",
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        "Terms of Service",
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Logout button
                Button(
                    onClick = {
                        onLogout()
                        scope.launch {
                            googleAuthUiClient.signOut()
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", color = Color.White)
                }

            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun ProfileDialogOption(label: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp)
    }
}
