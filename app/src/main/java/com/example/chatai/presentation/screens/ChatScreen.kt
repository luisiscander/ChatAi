package com.example.chatai.presentation.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.usecase.MessageValidationResult
import com.example.chatai.ui.theme.ChatAiTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf<Message?>(null) }
    
    // Load conversation history when screen opens
    LaunchedEffect(conversationId) {
        if (conversationId.isNotEmpty()) {
            viewModel.loadConversationHistory(conversationId)
        }
    }
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty() && uiState.isAutoScrollEnabled) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        } else if (uiState.messages.isNotEmpty() && !uiState.isAutoScrollEnabled) {
            // Show notification for new message when not auto-scrolling
            viewModel.onNewMessageReceived()
        }
    }
    
    // Detect manual scroll
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex > 0 && uiState.isAutoScrollEnabled) {
            viewModel.onManualScroll()
        }
    }
    
    // Load more messages when reaching the top
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex <= 2 && uiState.hasMoreMessages && !uiState.isLoadingMoreMessages) {
            viewModel.loadMoreMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.conversationTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Messages List
            Box(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Loading indicator for more messages
                    if (uiState.isLoadingMoreMessages) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                    
                    items(uiState.messages) { message ->
                        MessageBubble(
                            message = message,
                            onCopyMessage = viewModel::copyMessage,
                            onDeleteMessage = { 
                                messageToDelete = message
                                showDeleteDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Typing indicator
                    if (uiState.isTyping) {
                        item {
                            TypingIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Streaming message
                    if (uiState.isStreaming && uiState.streamingText.isNotEmpty()) {
                        item {
                            StreamingMessage(
                                text = uiState.streamingText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                // New message notification
                if (uiState.showNewMessageNotification) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            TextButton(
                                onClick = { viewModel.scrollToLatestMessage() }
                            ) {
                                Text(
                                    text = "Nuevo mensaje ↓",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
            
            // Message Input
            MessageInput(
                messageText = uiState.messageText,
                onMessageTextChanged = viewModel::onMessageTextChanged,
                onSendMessage = viewModel::sendMessage,
                validationResult = uiState.validationResult,
                isEnabled = uiState.isEnabled,
                error = uiState.error,
                isStreaming = uiState.isStreaming,
                canCancelStreaming = uiState.canCancelStreaming,
                onCancelStreaming = viewModel::cancelStreaming,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Copy success message
        uiState.copySuccessMessage?.let { successMessage ->
            LaunchedEffect(successMessage) {
                // Show toast-like message
                kotlinx.coroutines.delay(2000)
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog && messageToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    messageToDelete = null
                },
                title = { Text("Eliminar mensaje") },
                text = { Text("¿Estás seguro de que quieres eliminar este mensaje?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            messageToDelete?.let { message ->
                                viewModel.deleteMessage(message.id)
                            }
                            showDeleteDialog = false
                            messageToDelete = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            messageToDelete = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    onCopyMessage: (String) -> Unit = {},
    onDeleteMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isFromUser
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    var showContextMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = if (isUserMessage) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
            } else {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = if (isUserMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .weight(1f, fill = false)
                .combinedClickable(
                    onClick = { /* No-op for now */ },
                    onLongClick = { showContextMenu = true }
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = if (isUserMessage) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Copiar") },
                onClick = {
                    onCopyMessage(message.content)
                    showContextMenu = false
                }
            )
            // Issue #69: Eliminar mensaje propio
            if (isUserMessage) {
                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        onDeleteMessage(message.id)
                        showContextMenu = false
                    }
                )
            }
            // Issue #70: Eliminar mensaje del asistente
            if (!isUserMessage) {
                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        onDeleteMessage(message.id)
                        showContextMenu = false
                    }
                )
            }
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            Text(
                text = "escribiendo...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun StreamingMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            Text(
                text = text + "▊", // Cursor parpadeante
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    validationResult: MessageValidationResult,
    isEnabled: Boolean,
    error: String? = null,
    isStreaming: Boolean = false,
    canCancelStreaming: Boolean = false,
    onCancelStreaming: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Error message
        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Validation message
        when (validationResult) {
            is MessageValidationResult.TooLong -> {
                Text(
                    text = "El mensaje es muy largo (máximo 10,000 caracteres)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
            else -> { /* No validation message for Valid or Empty */ }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChanged,
                placeholder = { Text("Escribe tu mensaje...") },
                modifier = Modifier.weight(1f),
                enabled = isEnabled,
                maxLines = 4,
                isError = validationResult is MessageValidationResult.TooLong
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = if (isStreaming && canCancelStreaming) onCancelStreaming else onSendMessage,
                modifier = Modifier.size(48.dp),
                containerColor = if (isStreaming) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    if (isStreaming) Icons.Default.Close else Icons.Default.Send,
                    contentDescription = if (isStreaming) "Detener streaming" else "Enviar mensaje"
                )
            }
        }
        
        if (messageText.isNotBlank() && validationResult !is MessageValidationResult.TooLong) {
            Text(
                text = "${messageText.length}/10,000",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatAiTheme {
        ChatScreen(
            conversationId = "test",
            onBackClicked = { }
        )
    }
}