//package com.example.bookreaderapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class AuthViewModel : ViewModel() {
//    private val auth = FirebaseAuth.getInstance()
//    private val _user = MutableStateFlow(auth.currentUser)
//    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()
//
//    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                _user.value = auth.currentUser
//                onResult(it.isSuccessful)
//            }
//    }
//
//    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                _user.value = auth.currentUser
//                onResult(it.isSuccessful)
//            }
//    }
//
//    fun logout() {
//        auth.signOut()
//        _user.value = null
//    }
//}
