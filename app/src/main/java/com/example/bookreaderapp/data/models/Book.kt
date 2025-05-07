package com.example.bookreaderapp.data.models


data class Book(
    val id: String="",
    val title: String="",
    val author: String="",
    val pdfurl: String="",
    val genre: String="",
    val description: String = "",
    val coverUrl: String = ""
)
