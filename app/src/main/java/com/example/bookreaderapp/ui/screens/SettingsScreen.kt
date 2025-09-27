//package com.example.bookreaderapp.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.Logout
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SettingsScreen(
//    name: String,
//    email: String?,
//    isDarkMode: Boolean,
//    onToggleDarkMode: () -> Unit,
//    onLogoutClick: () -> Unit,
//    onBackClick: () -> Unit
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Settings") },
//                navigationIcon = {
//                    IconButton(onClick = { onBackClick() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//            // 🔹 Profile Section
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.Start
//            ) {
//                Text(
//                    text = name,
//                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = email ?: "Guest",
//                    fontSize = 14.sp,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            // 🔹 Dark Mode Toggle
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
//                Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode() })
//            }
//
//            // 🔹 App Info
//            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                Text("App Version: 1.0.0", style = MaterialTheme.typography.bodyMedium)
//                Text("About", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
//                Text("Help", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
//                Text("Feedback", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
//            }
//
//            Spacer(modifier = Modifier.weight(1f)) // Push logout to bottom
//
//            // 🔹 Logout Button
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onLogoutClick() }
//                    .padding(vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Logout,
//                    contentDescription = "Logout",
//                    tint = Color.Red
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Logout",
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
//                    color = Color.Red
//                )
//            }
//
//        }
//    }
//}
