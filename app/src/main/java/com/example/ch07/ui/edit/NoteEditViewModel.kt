package com.example.ch07.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditUiState(
    val title: String = "",
    val content: String = "",
    val tagsText: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val tagsInput: String = ""
)

class NoteEditViewModel(
    private val repository: NoteRepository,
    private val noteId: Int = 0,
    private val clock: () -> Long = System::currentTimeMillis
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditUiState(isLoading = noteId != 0))
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    // Catatan asli saat mode edit. Disimpan agar createdAt, isPinned, dan id
    // TIDAK ikut tereset saat catatan diperbarui.
    private var originalNote: NoteEntity? = null

    init {
        if (noteId != null && noteId != -1) {
            viewModelScope.launch {
                val note = repository.getNoteById(noteId)
                if (note != null) {
                    originalNote = note
                    _uiState.update {
                        it.copy(
                            title = note.title,
                            content = note.content,
                            tagsInput = note.tags.joinToString(", "),
                            isLoading = false
                        )
                    }
                } else{
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Catatan tidak ditemukan") }
                }
            }
        } else{
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, errorMessage = null) }

    fun onContentChange(value: String) =
        _uiState.update { it.copy(content = value) }

    fun onTagsChange(value: String) =
        _uiState.update { it.copy(tagsInput = value) }

    fun save() {
        val current = _uiState.value
        if (current.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val now = clock()
            val parsedTags = parseTags(current.tagsInput)
            val existing = originalNote

            if (existing == null) {
                repository.insertNote(
                    NoteEntity(
                        title = current.title.trim(),
                        content = current.content.trim(),
                        createdAt = now,
                        updatedAt = now,
                        tags = parsedTags
                    )
                )
            } else {
                repository.updateNote(
                    existing.copy(
                        title = current.title.trim(),
                        content = current.content.trim(),
                        updatedAt = now,
                        tags = parsedTags
                    )
                )
            }

            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
    private fun formatTags(tags: List<String>): String = tags.joinToString(",")

    private fun parseTags(tagsText:String): List<String> =
        tagsText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

}
