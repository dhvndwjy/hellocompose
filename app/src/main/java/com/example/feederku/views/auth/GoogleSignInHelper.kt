package com.example.feederku.views.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun signInWithGoogle(
    context: Context,
    webClientId: String = "814320278857-6dk33sq6guim5qnto1j2ui561g09ssn1.apps.googleusercontent.com"
): Result<String> {
    return try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = context
        )

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        Result.success(googleIdTokenCredential.idToken)
    } catch (e: GetCredentialCancellationException) {
        Result.failure(Exception("Login cancelled"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// Helper function used by Login and SignUp routes
suspend fun getGoogleIdToken(context: Context): Result<String> {
    return signInWithGoogle(context)
}
