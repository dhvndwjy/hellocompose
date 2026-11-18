package com.example.feederku.views.auth

import com.example.feederku.views.auth.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = null

    private data class FakeUser(val name: String, val password: String)
    private val users = mutableMapOf<String, FakeUser>() // Penyimpanan sementara dalam memori (RAM)

    override suspend fun signInWithEmail(email: String, password: String) = runCatching {
        delay(100) // Simulasi jeda jaringan
        val user = users[email.lowercase()]
            ?: error("Account is not registered. Please Sign Up first.")
        if (user.password != password) error("Incorrect email or password.")
        Unit
    }

    override suspend fun signUpWithEmail(name: String, email: String, password: String) = runCatching {
        delay(100)
        val key = email.lowercase()
        if (users.containsKey(key)) error("Email is already registered. Please Login.")
        users[key] = FakeUser(name, password)
        Unit
    }

    override suspend fun signInWithGoogle(idToken: String) = runCatching {
        delay(100)
        Unit
    }

    override fun signOut() {
        users.clear()
    }
}