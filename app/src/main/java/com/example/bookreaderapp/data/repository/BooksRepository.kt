package com.example.bookreaderapp.data.repository


import com.example.bookreaderapp.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.flow

class BooksRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllBooks(): Flow<List<Book>> = flow {
        val snapshot = firestore.collection("books").get().await()
        val books = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
        emit(books)
    }

    fun getBookById(bookId: String): Flow<Book?> = flow {
        val doc = firestore.collection("books").document(bookId).get().await()
        emit(doc.toObject(Book::class.java))
    }
}
