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
    val inLibrary: Boolean = false
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "author" to author,
            "genre" to genre,
            "coverurl" to coverurl,
            "category" to category
        ) as Map<String, Any>
    }
}
