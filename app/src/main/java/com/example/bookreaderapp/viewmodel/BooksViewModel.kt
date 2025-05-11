package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookreaderapp.data.models.Book
import com.example.bookreaderapp.data.repository.BooksRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await


class BooksViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books


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
