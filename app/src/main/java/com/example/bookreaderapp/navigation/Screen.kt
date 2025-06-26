package com.example.bookreaderapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Library : BottomNavItem("library", "Library", Icons.Default.MenuBook)
    object Wishlist : BottomNavItem("wishlist", "Wishlist", Icons.Default.Favorite)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}
