package com.example.chatai.presentation

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
class ConversationNavigationViewModel @Inject constructor(
    private val createConversationUseCase: CreateConversationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationNavigationUiState())
    val uiState: StateFlow<ConversationNavigationUiState> = _uiState.asStateFlow()

    fun createNewConversation(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)
            
            try {
                when (val result = createConversationUseCase()) {
                    is com.example.chatai.domain.usecase.CreateConversationResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            lastCreatedConversationId = result.conversation.id
                        )
                        // Add a small delay to ensure the conversation is properly stored
                        kotlinx.coroutines.delay(100)
                        onSuccess(result.conversation.id)
                    }
                    is com.example.chatai.domain.usecase.CreateConversationResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = "Error al crear conversación: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ConversationNavigationUiState(
    val isCreating: Boolean = false,
    val error: String? = null,
    val lastCreatedConversationId: String? = null
)
