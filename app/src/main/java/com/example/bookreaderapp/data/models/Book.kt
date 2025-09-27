package com.example.bookreaderapp.data.models


data class Book(
    val id: String="",
    val title: String="",
    val author: String="",
    val pdfurl: String="",
    val genre: String="",
    val description: String = "",
    val coverurl:String = "",
    val pages : Int = 0,
    val category: String? = null,
    val inLibrary: Boolean = false,
    val categories: List<String> = emptyList() // ⭐️ Important for filtering

) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "author" to author,
            "pdfurl" to pdfurl,
            "genre" to genre,
            "description" to description,
            "coverurl" to coverurl,
            "pages" to pages,
            "category" to category,         // Nullable field is fine here
            "inLibrary" to inLibrary,
            "categories" to categories      // List<String>
        )
    }
}