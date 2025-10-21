package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.UserPreferencesRepository
import com.example.chatai.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val copyMessageUseCase: CopyMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val streamAiResponseUseCase: StreamAiResponseUseCase,
    private val cancelStreamingUseCase: CancelStreamingUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val getMessagesWithPaginationUseCase: GetMessagesWithPaginationUseCase,
    private val checkNetworkConnectionUseCase: CheckNetworkConnectionUseCase,
    private val validateApiKeyConnectionUseCase: ValidateApiKeyConnectionUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
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
        
        if (currentText.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "El mensaje no puede estar vacío"
            )
            return
        }
        
        val validationResult = validateMessageUseCase(currentText)
        if (validationResult !is MessageValidationResult.Valid) {
            _uiState.value = _uiState.value.copy(
                error = "Mensaje inválido: $validationResult"
            )
            return
        }

        viewModelScope.launch {
            try {
                if (!checkNetworkConnectionUseCase()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Sin conexión a internet",
                        messageText = currentText
                    )
                    return@launch
                }

                val apiKey = userPreferencesRepository.getApiKey()
                
                if (apiKey.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Error de autenticación. Verifica tu API key"
                    )
                    return@launch
                }

                val apiKeyValidation = validateApiKeyConnectionUseCase(apiKey)
                when (apiKeyValidation) {
                    is ValidateApiKeyConnectionResult.Invalid -> {
                        _uiState.value = _uiState.value.copy(
                            error = "Error de autenticación. Verifica tu API key"
                        )
                        return@launch
                    }
                    is ValidateApiKeyConnectionResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = apiKeyValidation.message
                        )
                        return@launch
                    }
                    else -> { /* API key válida, continuar */ }
                }

                _uiState.value = _uiState.value.copy(
                    messageText = "",
                    validationResult = MessageValidationResult.Empty,
                    isTyping = true,
                    error = null,
                    isEnabled = false
                )

                val userMessage = Message(
                    id = UUID.randomUUID().toString(),
                    conversationId = _uiState.value.conversationId,
                    content = currentText,
                    isFromUser = true,
                    timestamp = Date(),
                    model = null
                )

                val sendResult = sendMessageUseCase(userMessage, apiKey)
                when (sendResult) {
                    is SendMessageResult.Success -> {
                        val currentMessages = _uiState.value.messages.toMutableList()
                        currentMessages.add(userMessage)

                        val newTitle = if (_uiState.value.conversationTitle == "New Conversation") {
                            userMessage.content.take(20)
                        } else {
                            _uiState.value.conversationTitle
                        }

                        _uiState.value = _uiState.value.copy(
                            messages = currentMessages,
                            isTyping = true,
                            conversationTitle = newTitle
                        )

                        _uiState.value = _uiState.value.copy(
                            isStreaming = true,
                            canCancelStreaming = true,
                            streamingText = "",
                            isTyping = false
                        )

                        cancelStreamingUseCase.reset()

                        streamAiResponseUseCase(userMessage, apiKey).collect { chunk ->
                            if (cancelStreamingUseCase.isCancelled()) {
                                return@collect
                            }

                            when (chunk) {
                                is StreamChunk.Text -> {
                                    _uiState.value = _uiState.value.copy(
                                        streamingText = _uiState.value.streamingText + chunk.content
                                    )
                                }
                                is StreamChunk.Complete -> {
                                    val aiMessage = Message(
                                        id = UUID.randomUUID().toString(),
                                        conversationId = userMessage.conversationId,
                                        content = _uiState.value.streamingText,
                                        isFromUser = false,
                                        timestamp = Date(),
                                        model = "gpt-4"
                                    )

                                    val updatedMessages = currentMessages.toMutableList()
                                    updatedMessages.add(aiMessage)

                                    _uiState.value = _uiState.value.copy(
                                        messages = updatedMessages,
                                        isStreaming = false,
                                        canCancelStreaming = false,
                                        streamingText = "",
                                        isEnabled = true,
                                        error = null
                                    )
                                }
                                is StreamChunk.Error -> {
                                    _uiState.value = _uiState.value.copy(
                                        isStreaming = false,
                                        canCancelStreaming = false,
                                        streamingText = "",
                                        isEnabled = true,
                                        error = chunk.message
                                    )
                                }
                            }
                        }
                    }
                    is SendMessageResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isTyping = false,
                            isEnabled = true,
                            error = sendResult.message,
                            messageText = currentText
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTyping = false,
                    isEnabled = true,
                    error = e.message ?: "Error inesperado al enviar mensaje",
                    messageText = currentText
                )
            }
        }
    }

    fun copyMessage(messageText: String) {
        val result = copyMessageUseCase(messageText)
        when (result) {
            is CopyMessageResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    copySuccessMessage = "Texto copiado"
                )
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = _uiState.value.copy(
                        copySuccessMessage = null
                    )
                }
            }
            is CopyMessageResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    error = result.message,
                    copySuccessMessage = null
                )
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val result = deleteMessageUseCase(messageId)
            when (result) {
                is DeleteMessageResult.Success -> {
                    val updatedMessages = _uiState.value.messages.filter { it.id != messageId }
                    _uiState.value = _uiState.value.copy(
                        messages = updatedMessages,
                        error = null
                    )
                }
                is DeleteMessageResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }
            }
        }
    }

    fun cancelStreaming() {
        cancelStreamingUseCase.cancelStreaming()
        
        val streamingText = _uiState.value.streamingText
        if (streamingText.isNotEmpty()) {
            val aiMessage = Message(
                id = UUID.randomUUID().toString(),
                conversationId = _uiState.value.conversationId,
                content = """$streamingText

[Respuesta interrumpida]""",
                isFromUser = false,
                timestamp = Date(),
                model = "gpt-4"
            )

            val updatedMessages = _uiState.value.messages.toMutableList()
            updatedMessages.add(aiMessage)

            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                isStreaming = false,
                canCancelStreaming = false,
                streamingText = "",
                isEnabled = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isStreaming = false,
                canCancelStreaming = false,
                streamingText = "",
                isEnabled = true
            )
        }
    }

    fun loadConversationHistory(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingHistory = true,
                conversationId = conversationId
            )

            try {
                val messages = getMessagesUseCase(conversationId).first()
                val title = if (messages.isNotEmpty()) {
                    messages.first().content.take(20)
                } else {
                    "New Conversation"
                }
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoadingHistory = false,
                    conversationTitle = title
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingHistory = false,
                    error = "Error al cargar historial: ${e.message}"
                )
            }
        }
    }

    fun initializeConversation(conversationId: String) {
        if (_uiState.value.conversationId != conversationId) {
            loadConversationHistory(conversationId)
        }
    }

    fun onManualScroll() {
        _uiState.value = _uiState.value.copy(
            isAutoScrollEnabled = false
        )
    }

    fun onNewMessageReceived() {
        if (!_uiState.value.isAutoScrollEnabled) {
            _uiState.value = _uiState.value.copy(
                showNewMessageNotification = true
            )
        }
    }

    fun scrollToLatestMessage() {
        _uiState.value = _uiState.value.copy(
            isAutoScrollEnabled = true,
            showNewMessageNotification = false
        )
    }

    fun loadMoreMessages() {
        if (_uiState.value.isLoadingMoreMessages || !_uiState.value.hasMoreMessages) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingMoreMessages = true
            )

            val result = getMessagesWithPaginationUseCase(
                conversationId = _uiState.value.conversationId,
                offset = _uiState.value.messages.size,
                limit = 20
            )

            when (result) {
                is GetMessagesWithPaginationResult.Success -> {
                    val currentMessages = _uiState.value.messages.toMutableList()
                    currentMessages.addAll(0, result.messages)

                    _uiState.value = _uiState.value.copy(
                        messages = currentMessages,
                        isLoadingMoreMessages = false,
                        hasMoreMessages = result.messages.size >= 20
                    )
                }
                is GetMessagesWithPaginationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingMoreMessages = false,
                        error = result.message
                    )
                }
            }
        }
    }

}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isTyping: Boolean = false,
    val isStreaming: Boolean = false,
    val streamingText: String = "",
    val canCancelStreaming: Boolean = false,
    val validationResult: MessageValidationResult = MessageValidationResult.Valid,
    val isEnabled: Boolean = true,
    val conversationId: String = "",
    val conversationTitle: String = "New Conversation",
    val error: String? = null,
    val copySuccessMessage: String? = null,
    val isLoadingHistory: Boolean = false,
    val isAutoScrollEnabled: Boolean = true,
    val showNewMessageNotification: Boolean = false,
    val isLoadingMoreMessages: Boolean = false,
    val hasMoreMessages: Boolean = true
)
