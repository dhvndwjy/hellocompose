package com.example.ch11.data.auth

import com.example.ch11.data.model.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            trySend(fa.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    override suspend fun login(email: String, password: String): Result<AuthUser> =
        safeCall { auth.signInWithEmailAndPassword(email, password).await().user!!.toAuthUser() }

    override suspend fun register(email: String, password: String): Result<AuthUser> =
        safeCall { auth.createUserWithEmailAndPassword(email, password).await().user!!.toAuthUser() }

    // T5.2
    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> =
        safeCall {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await().user!!.toAuthUser()
        }

    override fun logout() = auth.signOut()

    // Seperti runCatching, tapi pembatalan coroutine tidak ikut ditelan
    private inline fun <T> safeCall(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    // ASUMSI: AuthUser(uid, email). Sesuaikan dengan data/model/AuthUser.kt
    private fun FirebaseUser.toAuthUser() = AuthUser(uid = uid, email = email)
}