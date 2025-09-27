package com.example.bookreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreaderapp.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid
            if (userId != null) {
                db.collection("Profile").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Fetch existing profile with new fields
                            _profile.value = document.toObject(UserProfile::class.java)
                        } else {
                            // Create a fallback profile with default values
                            val fallback = UserProfile(
                                name = auth.currentUser?.displayName ?: "Reader",
                                email = auth.currentUser?.email ?: "",
                            )
                            db.collection("Profile").document(userId).set(fallback)
                            _profile.value = fallback
                        }
                        _isLoading.value = false
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileViewModel", "Error fetching profile: ", exception)
                        _profile.value = null
                        _isLoading.value = false
                    }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        onResult: () -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        val data = mapOf(
            "name" to name,
            "email" to email
        )

        db.collection("Profile").document(userId)
            .update(data)
            .addOnSuccessListener {
                fetchUserProfile()
                onResult()
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Error updating profile: ", exception)
            }
    }
//    fun toggleDarkMode(enabled: Boolean) {
//        viewModelScope.launch {
//            val userId = auth.currentUser?.uid
//            if (userId != null) {
//                _profile.value?.let { userProfile ->
//                    // Update the local state
//                    val updatedProfile = userProfile.copy(isDarkModeEnabled = enabled)
//                    _profile.value = updatedProfile
//
//                    // Update Firestore
//                    db.collection("Profile").document(userId)
//                        .update("isDarkModeEnabled", enabled)
//                        .addOnSuccessListener {
//                            Log.d("ProfileViewModel", "Dark mode updated successfully")
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.e("ProfileViewModel", "Error updating dark mode: ", exception)
//                        }
//                }
//            }
//        }
//    }


}
