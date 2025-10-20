package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.usecase.ArchiveConversationUseCase
import com.example.chatai.domain.usecase.GetConversationsUseCase
import com.example.chatai.domain.usecase.SearchConversationsUseCase
import com.example.chatai.domain.usecase.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val archiveConversationUseCase: ArchiveConversationUseCase,
    private val searchConversationsUseCase: SearchConversationsUseCase
) : ViewModel() {

    // Convert cold flow to hot flow using stateIn
    private val conversationsFlow = getConversationsUseCase(false)
        .catch { exception ->
            // Handle error in flow
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<SearchResult?>(null)
    val searchResult: StateFlow<SearchResult?> = _searchResult.asStateFlow()

    // Combined UI state
    val uiState: StateFlow<ConversationListUiState> = combine(
        conversationsFlow,
        isLoading,
        error,
        searchQuery,
        searchResult
    ) { conversations, loading, errorMsg, query, result ->
        ConversationListUiState(
            conversations = conversations,
            isLoading = loading,
            error = errorMsg,
            searchQuery = query,
            searchResult = result
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConversationListUiState()
    )

    fun archiveConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                archiveConversationUseCase(conversationId)
                // The conversations flow will automatically update
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun refreshConversations() {
        // Conversations flow will automatically refresh
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Trigger refresh by accessing the flow
                conversationsFlow.value
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun searchConversations(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                val result = searchConversationsUseCase(query)
                    .catch { exception ->
                        _error.value = exception.message
                        _searchResult.value = null
                    }
                    .first()
                
                _searchResult.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _searchResult.value = null
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResult.value = null
    }
}

data class ConversationListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResult: SearchResult? = null
)
