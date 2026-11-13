package com.example.ch11.data.note

import android.util.Log
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.model.Note
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreNoteRepo"

// Struktur data:  users/{uid}/notes/{noteId}
@Singleton
class FirestoreNoteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : NoteRepository {

    private fun notesCollection(uid: String): CollectionReference =
        firestore.collection("users").document(uid).collection("notes")

    // Listener mengikuti status login: ganti akun = ganti collection,
    // logout = daftar kosong (listener lama dilepas oleh flatMapLatest).
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNotes(): Flow<List<Note>> = authRepository.authState
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else notesSnapshots(uid) }

    // T3.2
    private fun notesSnapshots(uid: String): Flow<List<Note>> = callbackFlow {
        val registration = notesCollection(uid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.map { doc ->
                        noteFromMap(doc.id, doc.data ?: emptyMap(), doc.metadata.hasPendingWrites())
                    })
                }
            }
        awaitClose { registration.remove() }
    }

    // T3.3 Baca satu dokumen (get() saat offline otomatis membaca cache)
    override suspend fun getNote(id: String): Note? {
        val uid = authRepository.currentUser?.uid ?: return null
        val doc = notesCollection(uid).document(id).get().await()
        return doc.data?.let { noteFromMap(doc.id, it, doc.metadata.hasPendingWrites()) }
    }

    // TANPA await(): masuk cache lokal seketika, tidak menggantung saat offline
    override fun addNote(note: Note): Result<String> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(NotSignedInException())
        val docRef = notesCollection(uid).document()
        docRef.set(note.toFirestoreMap()).logFailure("tambah catatan")
        return Result.success(docRef.id)
    }

    override fun updateNote(note: Note): Result<Unit> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(NotSignedInException())
        notesCollection(uid).document(note.id).set(note.toFirestoreMap()).logFailure("update catatan")
        return Result.success(Unit)
    }

    override fun deleteNote(id: String): Result<Unit> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(NotSignedInException())
        notesCollection(uid).document(id).delete().logFailure("hapus catatan")
        return Result.success(Unit)
    }

    private fun Task<Void>.logFailure(action: String) {
        addOnFailureListener { e -> Log.w(TAG, "$action ditolak server: ${e.message}") }
    }
}

class NoteAccessException(message: String, cause: Throwable? = null) : Exception(message, cause)

@Suppress("unused")
private fun firestoreErrorMessage(e: FirebaseFirestoreException): String = when (e.code) {
    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
        "Akses ditolak server. Periksa Security Rules di Firebase Console."
    FirebaseFirestoreException.Code.UNAVAILABLE -> "Server tidak dapat dihubungi."
    else -> e.message ?: "Gagal memuat catatan."
}
