package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookreaderapp.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await


open class BooksViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    open val books: StateFlow<List<Book>> = _books

    private val _wishlist = MutableStateFlow<List<Book>>(emptyList())
    val wishlist: StateFlow<List<Book>> = _wishlist

    fun isInWishlist(book: Book): Boolean {
        return _wishlist.value.any { it.id == book.id }
    }

    fun toggleWishlist(book: Book) {
        val current = _wishlist.value.toMutableList()
        if (current.any { it.id == book.id }) {
            current.removeAll { it.id == book.id }
        } else {
            current.add(book)
        }
        _wishlist.value = current
    }


    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        firestore.collection("books")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e("BooksViewModel", "Error fetching books", exception)
                    return@addSnapshotListener
                }
                Log.d("BooksViewModel", "Books fetched: ${_books.value.size}")

                val bookList = snapshot.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)
                    book?.copy(id = doc.id) // attach document ID
                }
                _books.value = bookList
            }
    }

    fun getBookById(bookId: String): Flow<Book?> = flow {
        try {
            val doc = firestore.collection("books").document(bookId).get().await()
            if (doc.exists()) {
                val book = doc.toObject(Book::class.java)?.copy(id = doc.id)
                emit(book)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            Log.e("BooksViewModel", "Failed to get book by ID", e)
            emit(null)
        }
    }

    fun getBooksByGenre(genre: String): Flow<List<Book>> {
        return books.map { list -> list.filter { it.genre.equals(genre, ignoreCase = true) } }
    }


}