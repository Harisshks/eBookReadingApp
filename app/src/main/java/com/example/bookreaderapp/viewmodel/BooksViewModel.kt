package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookreaderapp.data.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BooksViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    open val books: StateFlow<List<Book>> = _books

    private val _wishlist = MutableStateFlow<List<Book>>(emptyList())
    val wishlist: StateFlow<List<Book>> = _wishlist

    private val _library = MutableStateFlow<List<Book>>(emptyList())
    val library: StateFlow<List<Book>> = _library
    private val _libraryIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        fetchBooks()
        fetchWishlist()
        fetchLibrary()
    }

    // ---------------- Wishlist ----------------
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

    // ---------------- Books ----------------
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

    // ---------------- Library ----------------
    fun toggleLibrary(book: Book, category: String) {
        val uid = auth.currentUser?.uid ?: return
        val libraryRef = firestore.collection("users")
            .document(uid)
            .collection("library")
            .document(book.id)

        libraryRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingCategory = snapshot.getString("category")
                Log.d("BooksViewModel", "Existing Category: $existingCategory, Clicked Category: $category")

                when {
                    // 1. If clicked the SAME category → remove
                    existingCategory != null && existingCategory == category -> {
                        libraryRef.delete()
                            .addOnSuccessListener {
                                Log.d("BooksViewModel", "Removed ${book.title} from library")
                            }
                            .addOnFailureListener {
                                Log.e("BooksViewModel", "Failed to remove from library", it)
                            }
                    }

                    // 2. If clicked a DIFFERENT category → update
                    else -> {
                        libraryRef.update("category", category)
                            .addOnSuccessListener {
                                Log.d("BooksViewModel", "Updated ${book.title} category to $category")
                            }
                            .addOnFailureListener {
                                Log.e("BooksViewModel", "Failed to update category", it)
                            }
                    }
                }
            } else {
                // 3. If not in library → add
                Log.d("BooksViewModel", "Book not found in library, adding...")
                val bookWithCategory = book.copy(category = category)
                libraryRef.set(bookWithCategory)
                    .addOnSuccessListener {
                        Log.d("BooksViewModel", "Added ${book.title} to library in $category")
                    }
                    .addOnFailureListener {
                        Log.e("BooksViewModel", "Failed to add to library", it)
                    }
            }
        }
    }


    fun fetchLibrary() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("library")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("BooksViewModel", "Error fetching library", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { it.toObject(Book::class.java)?.copy(id = it.id) }
                    _library.value = books
                    _libraryIds.value = books.map { it.id }.toSet()
                    Log.d("BooksViewModel", "Library updated: ${books.size} books")
                }
            }
    }

    fun removeFromLibrary(bookId: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("library")
            .document(bookId)
            .delete()
            .addOnSuccessListener {
                Log.d("BooksViewModel", "Book removed from library: $bookId")
            }
            .addOnFailureListener {
                Log.e("BooksViewModel", "Failed to remove book: ${it.message}")
            }
    }
    fun isBookInLibrary(bookId: String): Boolean {
        return _library.value.any { it.id == bookId }
    }
}


