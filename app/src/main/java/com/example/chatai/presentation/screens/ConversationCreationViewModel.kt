package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.CreateConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationCreationViewModel @Inject constructor(
    private val createConversationUseCase: CreateConversationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationCreationUiState())
    val uiState: StateFlow<ConversationCreationUiState> = _uiState.asStateFlow()

    fun createConversation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = createConversationUseCase()
                when (result) {
                    is com.example.chatai.domain.usecase.CreateConversationResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            conversationId = result.conversation.id,
                            error = null
                        )
                    }
                    is com.example.chatai.domain.usecase.CreateConversationResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido al crear conversación"
                )
            }
        }
    }

    fun clearState() {
        _uiState.value = ConversationCreationUiState()
    }
}

data class ConversationCreationUiState(
    val isLoading: Boolean = false,
    val conversationId: String? = null,
    val error: String? = null
)
