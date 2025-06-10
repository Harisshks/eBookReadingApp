package com.example.bookreaderapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

//sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
//    object Home : Screen("home", "Home", Icons.Filled.Home)
//    object Library : Screen("library", "Library", Icons.Filled.Book)
//    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
//}

// NavigationItem.kt
sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Library : BottomNavItem("library", "Library", Icons.Default.MenuBook)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}
