package com.example.feederku.views.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.feederku.model.FeedPost
import com.example.feederku.views.community.data.ChatRepository
import com.example.feederku.views.community.data.FirebaseChatRepository
import com.example.feederku.views.community.data.GroupItem
import com.example.feederku.views.community.model.ChatUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val limit = MutableStateFlow(PAGE_SIZE)
    private var observeMessagesJob: Job? = null
    private var observePostsJob: Job? = null

    private val _groups = MutableStateFlow<List<GroupItem>>(emptyList())
    val groups: StateFlow<List<GroupItem>> = _groups.asStateFlow()

    private val _posts = MutableStateFlow<List<FeedPost>>(emptyList())
    val posts: StateFlow<List<FeedPost>> = _posts.asStateFlow()

    private val _uiState = MutableStateFlow(
        ChatUiState(currentUserId = repository.currentUserId)
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
        selectGroup("default_group")
    }

    private fun loadGroups() {
        viewModelScope.launch {
            repository.observeGroups().collect { groupList ->
                _groups.value = groupList
            }
        }
    }

    fun selectGroup(groupId: String) {
        startObservingMessages(groupId)
        startObservingPosts(groupId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObservingMessages(groupId: String) {
        observeMessagesJob?.cancel()
        observeMessagesJob = viewModelScope.launch {
            limit
                .flatMapLatest { n ->
                    repository.observeMessages(groupId, n).map { list -> n to list }
                }
                .catch {
                    _uiState.update { s ->
                        s.copy(
                            isLoading = false,
                            loadFailed = true,
                            errorMessage = "Couldn't load messages."
                        )
                    }
                }
                .collect { (n, list) ->
                    _uiState.update { s ->
                        s.copy(
                            messages = list,
                            isLoading = false,
                            loadFailed = false,
                            canLoadMore = list.size >= n
                        )
                    }
                }
        }
    }

    private fun startObservingPosts(groupId: String) {
        observePostsJob?.cancel()
        observePostsJob = viewModelScope.launch {
            repository.observePosts(groupId).collect { postList ->
                _posts.value = postList
            }
        }
    }

    fun createGroup(name: String, initials: String) {
        viewModelScope.launch {
            try {
                repository.createGroup(name, initials)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to create group") }
            }
        }
    }

    fun createPost(groupId: String, content: String) {
        viewModelScope.launch {
            try {
                repository.createPost(groupId, content)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to create post") }
            }
        }
    }

    fun retry(groupId: String) {
        _uiState.update { it.copy(isLoading = true, loadFailed = false) }
        startObservingMessages(groupId)
    }

    fun onLoadOlderClick(groupId: String) {
        if (_uiState.value.isLoadingOlder) return
        _uiState.update { it.copy(isLoadingOlder = true) }
        limit.update { it + PAGE_SIZE }
        startObservingMessages(groupId)
    }

    fun onInputChange(text: String) {
        if (text.length > MAX_MESSAGE_LENGTH) return
        _uiState.update { it.copy(input = text) }
    }

    fun onSendClick(groupId: String) {
        val text = _uiState.value.input.trim()
        if (text.isEmpty() || text.length > MAX_MESSAGE_LENGTH) return

        _uiState.update { it.copy(input = "") }
        viewModelScope.launch {
            try {
                repository.sendMessage(groupId, text)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { s ->
                    s.copy(
                        input = if (s.input.isBlank()) text else s.input,
                        errorMessage = "Message failed to send."
                    )
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        const val PAGE_SIZE = 50
        const val MAX_MESSAGE_LENGTH = 500

        val Factory = viewModelFactory {
            initializer { ChatViewModel(FirebaseChatRepository()) }
        }
    }
}
