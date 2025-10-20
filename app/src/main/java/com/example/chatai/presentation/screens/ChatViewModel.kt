package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onMessageTextChanged(text: String) {
        val validationResult = validateMessageUseCase(text)
        
        _uiState.value = _uiState.value.copy(
            messageText = text,
            validationResult = validationResult,
            isEnabled = validationResult is MessageValidationResult.Valid || text.isBlank()
        )
    }

    fun sendMessage() {
        val currentText = _uiState.value.messageText
        if (currentText.isBlank()) return
        
        val validationResult = validateMessageUseCase(currentText)
        if (validationResult !is MessageValidationResult.Valid) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                messageText = "",
                validationResult = MessageValidationResult.Empty,
                isTyping = true,
                error = null
            )

            // Simular envío de mensaje
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                conversationId = _uiState.value.conversationId,
                content = currentText,
                isFromUser = true,
                timestamp = Date(),
                model = null
            )

            val currentMessages = _uiState.value.messages.toMutableList()
            currentMessages.add(userMessage)

            _uiState.value = _uiState.value.copy(
                messages = currentMessages,
                isTyping = true
            )

            // Simular respuesta de IA
            kotlinx.coroutines.delay(2000)

            val aiMessage = Message(
                id = UUID.randomUUID().toString(),
                conversationId = _uiState.value.conversationId,
                content = "Hola! Soy un asistente de IA. ¿En qué puedo ayudarte?",
                isFromUser = false,
                timestamp = Date(),
                model = "gpt-4"
            )

            val updatedMessages = currentMessages.toMutableList()
            updatedMessages.add(aiMessage)

            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                isTyping = false,
                isEnabled = true
            )
        }
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isTyping: Boolean = false,
    val validationResult: MessageValidationResult = MessageValidationResult.Valid,
    val isEnabled: Boolean = true,
    val conversationId: String = "dummy_chat_id",
    val conversationTitle: String = "Chat",
    val error: String? = null
)
