package com.example.ch11.data.note

import com.example.ch11.data.model.Note

// Nama field dokumen Firestore. Ditulis eksplisit (bukan toObject()) agar
// sama persis dengan yang diperiksa di firestore.rules.
const val FIELD_TITLE = "title"
const val FIELD_CONTENT = "content"
const val FIELD_CREATED_AT = "createdAt"
const val FIELD_UPDATED_AT = "updatedAt"
const val FIELD_IS_PINNED = "isPinned"
const val FIELD_TAGS = "tags"

// id tidak disimpan di dalam dokumen: id adalah nama dokumen itu sendiri.
// hasPendingWrites juga tidak: itu status cache di perangkat.

fun Note.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "content" to content,
    "isPinned" to isPinned,
    "tags" to tags,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)   // tanpa id dan hasPendingWrites

fun noteFromMap(id: String, data: Map<String, Any?>, hasPendingWrites: Boolean = false): Note {
    val created = (data["createdAt"] as? Number)?.toLong() ?: 0L
    val updated = (data["updatedAt"] as? Number)?.toLong() ?: created
    return Note(
        id = id,
        title = data["title"] as? String ?: "",
        content = data["content"] as? String ?: "",
        isPinned = data["isPinned"] as? Boolean ?: false,
        tags = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        createdAt = created,
        updatedAt = updated,
        hasPendingWrites = hasPendingWrites
    )
}

// Sudah disediakan. Firestore mengurutkan berdasarkan createdAt. Catatan yang disematkan
// dipindah ke atas di perangkat: orderBy dua field butuh composite index.
fun sortNotes(notes: List<Note>) =
    notes.sortedWith(
        compareByDescending<Note> { it.isPinned }
            .thenByDescending { it.createdAt }
    )
