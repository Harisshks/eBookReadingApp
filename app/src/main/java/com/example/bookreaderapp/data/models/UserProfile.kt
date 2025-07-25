package com.example.bookreaderapp.data.models

class UserProfile (
        val name: String = "",
        val email: String = "",
        val profileImageUrl: String = "",
        val booksRead: Int = 0,
        val booksInProgress: Int = 0,
        val favoritesCount: Int = 0
)