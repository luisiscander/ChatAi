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
            
            when (val result = createConversationUseCase()) {
                is com.example.chatai.domain.usecase.CreateConversationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        lastCreatedConversationId = result.conversation.id
                    )
                    onSuccess(result.conversation.id)
                }
                is com.example.chatai.domain.usecase.CreateConversationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = result.message
                    )
                }
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
