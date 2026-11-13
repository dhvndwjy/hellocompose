package com.example.ch12.data.photo

import com.example.ch12.data.auth.AuthRepository
import com.example.ch12.data.note.NotSignedInException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Struktur berkas:  users/{uid}/profile.jpg  (sejajar dengan path Firestore)
@Singleton
class FirebasePhotoStorage @Inject constructor(
    private val storage: FirebaseStorage,
    private val authRepository: AuthRepository
) : PhotoStorage {

    override fun uploadProfilePhoto(bytes: ByteArray): Flow<UploadEvent> = callbackFlow {
        val uid = authRepository.currentUser?.uid ?: throw NotSignedInException()
        val ref = storage.reference.child("users/$uid/profile.jpg")
        val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
        val task = ref.putBytes(bytes, metadata)
            .addOnProgressListener { s ->
                trySend(UploadEvent.Progress(s.bytesTransferred, s.totalByteCount))
            }
            .addOnSuccessListener {
                launch {
                    try {
                        val downloadUrl = ref.downloadUrl.await().toString()
                        send(UploadEvent.Done(downloadUrl))
                        close()
                    } catch (e: Exception) {
                        close(PhotoUploadException(storageErrorMessage(e), e))
                    }
                }
            }
            .addOnFailureListener { e ->
                close(PhotoUploadException(storageErrorMessage(e), e))
            }

        awaitClose {
            if (!task.isComplete) {
                task.cancel()
            }
        }
    }
}

@Suppress("unused")
private fun storageErrorMessage(e: Exception): String =
    if (e is StorageException) storageErrorMessageForCode(e.errorCode)
    else e.message ?: "Upload gagal. Coba lagi."
