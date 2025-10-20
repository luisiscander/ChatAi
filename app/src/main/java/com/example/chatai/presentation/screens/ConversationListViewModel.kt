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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val archiveConversationUseCase: ArchiveConversationUseCase,
    private val searchConversationsUseCase: SearchConversationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Use first() instead of collect() for StateFlow
                val conversations = getConversationsUseCase(false).catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }.first()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    conversations = conversations,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun archiveConversation(conversationId: String) {
        viewModelScope.launch {
            archiveConversationUseCase(conversationId)
            // Recargar conversaciones después de archivar
            loadConversations()
        }
    }

    fun refreshConversations() {
        loadConversations()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun searchConversations(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(searchQuery = query)
                
                // Use first() instead of collect() for StateFlow
                val searchResult = searchConversationsUseCase(query)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = exception.message,
                            searchResult = null
                        )
                    }.first()
                
                _uiState.value = _uiState.value.copy(
                    searchResult = searchResult,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    searchResult = null
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResult = null
        )
        loadConversations()
    }
}

data class ConversationListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResult: SearchResult? = null
)
