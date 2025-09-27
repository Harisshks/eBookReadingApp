package com.example.bookreaderapp.viewmodel

import android.content.Context
import com.example.bookreaderapp.data.models.UserProfile
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthUiClient(
    context: Context
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val oneTapClient: SignInClient = Identity.getSignInClient(context)

    fun beginSignIn(onResult: (BeginSignInResult?, Exception?) -> Unit) {
        oneTapClient.signOut().addOnCompleteListener {
            val signInRequest = BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(
                            "353347824081-d82uu9ugb8igl77rrdrbr47q6b7p6es5.apps.googleusercontent.com"
                        )
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(false)
                .build()

            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    onResult(result, null)
                }
                .addOnFailureListener { e ->
                    onResult(null, e)
                }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
    }

    /** ✅ Return logged-in user profile */
    fun getSignedInUser(): UserProfile? {
        val user = auth.currentUser
        return if (user != null) {
            UserProfile(
                name = user.displayName ?: "",
                email = user.email ?: "",
                profileImageUrl = user.photoUrl?.toString() ?: ""
            )
        } else null
    }
}
