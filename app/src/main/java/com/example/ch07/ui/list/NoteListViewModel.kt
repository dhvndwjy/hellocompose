package com.example.ch07.ui.list

import android.provider.CalendarContract.Attendees.query
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

data class NoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    val recentlyDeleted: NoteEntity? = null,
    val selectedTag: String? = null,
    val availableTags: List<String> = emptyList(),
    val errorMessage: String? = null
) {
    // Turunan: sedang mencari/memfilter atau tidak (menentukan pesan kosong).
    val isFiltering: Boolean
        get() = query.isNotBlank() || selectedTag != null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Teks yang sedang diketik (langsung tampil di TextField)
    private val queryText = MutableStateFlow("")
    private val loadError = MutableStateFlow<String?>(null)
    private val recentlyDeleted = MutableStateFlow<NoteEntity?> (null)
    private val selectedTag = MutableStateFlow<String?>(null)

    // Query ke database ditunda 300 ms setelah pengguna berhenti mengetik
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // Query kosong tidak ditunda: daftar penuh tampil segera.
    private val searchResults: Flow<List<NoteEntity>> = queryText
        .debounce { query ->
            if (query.isBlank()) 0L else 300L
        }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllNotes()
            else repository.searchNotes(query)
        }
        .catch { e ->
            loadError.value = e.message ?: "Gagal memuat catatan"
            emit(emptyList())
        }

    // UiState DITURUNKAN dari beberapa sumber (pola Chapter 6).
    private val allNotesFlow: Flow<List<NoteEntity>> = repository.getAllNotes()
        .catch { emit(emptyList()) }
    val uiState: StateFlow<NoteListUiState> = combine(
        searchResults, queryText, selectedTag, recentlyDeleted, allNotesFlow
    ) { results, query, tag, deleted, allNotes ->

        val availableTags = allNotes.flatMap { it.tags }.distinct().sorted()
        val activeTag = tag?.takeIf { it in availableTags }
        val filtered = results
            .filter { note -> activeTag == null || activeTag in note.tags }
            .sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.updatedAt })

    NoteListUiState(notes = filtered, query = query, recentlyDeleted = deleted, availableTags = availableTags,selectedTag = activeTag, errorMessage = loadError.value)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NoteListUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        queryText.value = newQuery
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            recentlyDeleted.value = note
            repository.deleteNote(note) }
    }

    fun undoDelete(){
        val noteToRestore = recentlyDeleted.value
        if (noteToRestore != null){
            viewModelScope.launch{
                repository.insertNote(noteToRestore)
                recentlyDeleted.value = null
            }
        }
    }
    fun onUndoDismissed(){
        recentlyDeleted.value = null
    }

    fun onTagSelected(tag: String?){
        selectedTag.value = if(tag == selectedTag.value) null else tag
    }

    // Menyematkan tidak mengubah updatedAt (bukan perubahan isi catatan)
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
