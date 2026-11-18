package com.example.feederku.views.auth.data

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isUserLoggedIn: Boolean get() = currentUser != null
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    fun signOut()
}

