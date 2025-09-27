package com.example.bookreaderapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bookreaderapp.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel:ViewModel(){

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _currUser = MutableStateFlow<UserProfile?>(null)
    val currUser: StateFlow<UserProfile?> = _currUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user != null) {
                loadUserProfile(user.uid)
                _authState.value = AuthState.Authenticated
            } else {
                _currUser.value = null
                _authState.value = AuthState.Idle
            }
        }
    }

    fun loadUserProfile(uid: String) {
        db.collection("Profile").document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    _currUser.value = profile
                } else {
                    val user = auth.currentUser
                    val fallbackName =
                        user?.displayName ?: user?.email?.substringBefore("@") ?: "Guest"
                    val fallbackEmail = user?.email ?: "guest@example.com"
                    _currUser.value = UserProfile(name = fallbackName, email = fallbackEmail)
                }
            }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _currUser.value = null
        _authState.value = AuthState.Idle
    }
}
