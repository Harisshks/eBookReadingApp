package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreaderapp.data.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await


class BooksViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _wishlist = MutableStateFlow<List<Book>>(emptyList())
    val wishlist: StateFlow<List<Book>> = _wishlist

    init {
        fetchBooks()
        fetchWishlist()
    }

    // ✅ Firestore Wishlist Listener
    private fun fetchWishlist() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BooksViewModel", "Wishlist listen failed", error)
                    return@addSnapshotListener
                }

                val wishlistBooks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Book::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _wishlist.value = wishlistBooks
                Log.d("BooksViewModel", "Wishlist updated: ${wishlistBooks.size} books")
            }
    }

    fun isInWishlist(book: Book): Boolean {
        return _wishlist.value.any { it.id == book.id }
    }

    fun toggleWishlist(book: Book) {
        val userId = auth.currentUser?.uid ?: return
        val wishlistRef = firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .document(book.id)

        if (isInWishlist(book)) {
            wishlistRef.delete()
                .addOnSuccessListener {
                    Log.d("BooksViewModel", "Removed from wishlist: ${book.title}")
                }
                .addOnFailureListener {
                    Log.e("BooksViewModel", "Failed to remove from wishlist", it)
                }
        } else {
            wishlistRef.set(book)
                .addOnSuccessListener {
                    Log.d("BooksViewModel", "Added to wishlist: ${book.title}")
                }
                .addOnFailureListener {
                    Log.e("BooksViewModel", "Failed to add to wishlist", it)
                }
        }
    }

    private fun fetchBooks() {
        firestore.collection("books")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e("BooksViewModel", "Error fetching books", exception)
                    return@addSnapshotListener
                }

                val bookList = snapshot.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)
                    book?.copy(id = doc.id)
                }
                _books.value = bookList
            }
    }


    // 🔍 5. Get book by ID
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