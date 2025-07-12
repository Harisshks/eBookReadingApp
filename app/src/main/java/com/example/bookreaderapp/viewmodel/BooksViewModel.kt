package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookreaderapp.data.models.Book
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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

    val _library = MutableStateFlow<List<Book>>(emptyList())
    val library: StateFlow<List<Book>> = _library


    init {
        fetchBooks()
        fetchWishlist()
        fetchLibrary()
    }

    //Firestore Wishlist Listener
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


    fun addToLibrary(book: Book, category: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        val bookData = hashMapOf(
            "id" to book.id,
            "title" to book.title,
            "author" to book.author,
            "coverurl" to book.coverurl,
            "genre" to book.genre,
            "category" to category
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("library")
            .document(book.id)
            .set(bookData)
            .addOnSuccessListener {
                Log.d("Library", "Book added to library with category $category")
            }
            .addOnFailureListener {
                Log.e("Library", "Failed to add book: ${it.message}")
            }
    }

    fun toggleLibrary(book: Book, category: String) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val docRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("library")
                .document(book.id)

            docRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Remove from library
                    docRef.delete()
                        .addOnSuccessListener {
                            Log.d("Library", "Book removed from library")
                        }
                        .addOnFailureListener {
                            Log.e("Library", "Failed to remove book: ${it.message}")
                        }
                } else {
                    // Add to library
                    val bookMap = book.toMap().toMutableMap()
                    bookMap["category"] = category
                    docRef.set(bookMap)
                        .addOnSuccessListener {
                            Log.d("Library", "Book added to library")
                        }
                        .addOnFailureListener {
                            Log.e("Library", "Failed to add book: ${it.message}")
                        }
                }
            }
        }
    }

    fun removeFromLibrary(bookId: String) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("library")
                .document(bookId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Library", "Book removed from library")
                }
                .addOnFailureListener {
                    Log.e("Library", "Failed to remove book: ${it.message}")
                }
        }
    }



    fun fetchLibrary() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("library")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
                    _library.value = books
                }
            }
    }

}