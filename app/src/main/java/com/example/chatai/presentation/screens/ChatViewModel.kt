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
    private val getConversationStatisticsUseCase: GetConversationStatisticsUseCase,
    private val checkUsageLimitUseCase: CheckUsageLimitUseCase,
    private val saveMessageDraftUseCase: SaveMessageDraftUseCase,
    private val getMessageDraftUseCase: GetMessageDraftUseCase,
    private val clearMessageDraftUseCase: ClearMessageDraftUseCase,
    private val shareMessageUseCase: ShareMessageUseCase,
    private val shareConversationUseCase: ShareConversationUseCase,
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
                // Issue #117: Verificar conexión antes de enviar
                if (!checkNetworkConnectionUseCase()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Sin conexión a internet",
                        messageText = currentText,
                        showDraftSaveOption = true
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

                        streamAiResponseUseCase(userMessage, apiKey, _uiState.value.selectedModel).collect { chunk ->
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
                                        model = _uiState.value.selectedModel,
                                        inputTokens = chunk.inputTokens,
                                        outputTokens = chunk.outputTokens,
                                        totalTokens = chunk.totalTokens,
                                        estimatedCost = chunk.estimatedCost
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
                                    
                                    // Limpiar borrador después de enviar exitosamente
                                    clearDraft()
                                    
                                    // Check usage limit after message is sent (Issue #115)
                                    checkUsageLimit()
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
            // Issue #117: Cargar borrador si existe
            loadDraft(conversationId)
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

            // Issue #131: Updated to use new pagination API
            val result = getMessagesWithPaginationUseCase(
                conversationId = _uiState.value.conversationId,
                limit = 50
            )

            when (result) {
                is GetMessagesWithPaginationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        messages = result.messages,
                        isLoadingMoreMessages = false,
                        hasMoreMessages = result.hasMore
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
    
    fun showStatistics() {
        viewModelScope.launch {
            val result = getConversationStatisticsUseCase(_uiState.value.conversationId)
            when (result) {
                is ConversationStatisticsResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        showStatistics = true,
                        statistics = result.statistics
                    )
                }
                is ConversationStatisticsResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun hideStatistics() {
        _uiState.value = _uiState.value.copy(
            showStatistics = false
        )
    }
    
    private fun checkUsageLimit() {
        viewModelScope.launch {
            when (val result = checkUsageLimitUseCase()) {
                is UsageLimitResult.HighUsage -> {
                    _uiState.value = _uiState.value.copy(
                        usageAlert = UsageAlert(
                            message = "Has usado $%.2f de tu límite de $%.2f".format(
                                result.currentUsage, 
                                result.limit
                            ),
                            currentUsage = result.currentUsage,
                            limit = result.limit,
                            isExceeded = false
                        )
                    )
                }
                is UsageLimitResult.LimitExceeded -> {
                    _uiState.value = _uiState.value.copy(
                        usageAlert = UsageAlert(
                            message = "Has excedido tu límite mensual de $%.2f".format(result.limit),
                            currentUsage = result.currentUsage,
                            limit = result.limit,
                            isExceeded = true
                        )
                    )
                }
                else -> {
                    // Normal usage, no alert needed
                }
            }
        }
    }
    
    fun dismissUsageAlert() {
        _uiState.value = _uiState.value.copy(usageAlert = null)
    }
    
    fun checkNetworkConnection(): Boolean {
        return checkNetworkConnectionUseCase()
    }
    
    // Issue #117: Guardar mensaje como borrador
    fun saveAsDraft() {
        viewModelScope.launch {
            try {
                val result = saveMessageDraftUseCase(_uiState.value.conversationId, _uiState.value.messageText)
                when (result) {
                    is SaveDraftResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            showDraftSaveOption = false,
                            error = null,
                            copySuccessMessage = "Borrador guardado"
                        )
                        kotlinx.coroutines.delay(2000)
                        _uiState.value = _uiState.value.copy(copySuccessMessage = null)
                    }
                    is SaveDraftResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al guardar borrador"
                )
            }
        }
    }
    
    // Cargar borrador guardado
    private fun loadDraft(conversationId: String) {
        viewModelScope.launch {
            try {
                val draft = getMessageDraftUseCase(conversationId)
                if (!draft.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        messageText = draft,
                        hasDraft = true
                    )
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    // Limpiar borrador después de enviar
    private fun clearDraft() {
        viewModelScope.launch {
            try {
                clearMessageDraftUseCase(_uiState.value.conversationId)
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    fun dismissDraftOption() {
        _uiState.value = _uiState.value.copy(showDraftSaveOption = false)
    }
    
    // Issue #120: Compartir mensaje individual
    fun shareMessage(context: android.content.Context, message: Message) {
        val result = shareMessageUseCase(context, message.content, message.model)
        when (result) {
            is ShareResult.Success -> {
                // Success, no need to show message
            }
            is ShareResult.Error -> {
                _uiState.value = _uiState.value.copy(error = result.message)
            }
        }
    }
    
    // Issue #121: Compartir conversación completa
    fun shareConversation(context: android.content.Context) {
        val result = shareConversationUseCase(
            context,
            _uiState.value.conversationTitle,
            _uiState.value.messages
        )
        when (result) {
            is ShareResult.Success -> {
                // Success, no need to show message
            }
            is ShareResult.Error -> {
                _uiState.value = _uiState.value.copy(error = result.message)
            }
        }
    }
    
    // Cambiar modelo seleccionado
    fun changeModel(modelId: String) {
        _uiState.value = _uiState.value.copy(selectedModel = modelId)
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
    val selectedModel: String = "google/gemini-2.0-flash-exp:free", // Default to free Google model
    val error: String? = null,
    val copySuccessMessage: String? = null,
    val isLoadingHistory: Boolean = false,
    val isAutoScrollEnabled: Boolean = true,
    val showNewMessageNotification: Boolean = false,
    val isLoadingMoreMessages: Boolean = false,
    val hasMoreMessages: Boolean = true,
    val showStatistics: Boolean = false,
    val statistics: ConversationStatistics? = null,
    val usageAlert: UsageAlert? = null,
    val showDraftSaveOption: Boolean = false,
    val hasDraft: Boolean = false
)

data class UsageAlert(
    val message: String,
    val currentUsage: Double,
    val limit: Double,
    val isExceeded: Boolean
)
