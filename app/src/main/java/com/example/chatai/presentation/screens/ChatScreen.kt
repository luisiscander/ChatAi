package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )
        
        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(
                    message = message,
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
        }
        
        // Message Input
        MessageInput(
            messageText = uiState.messageText,
            onMessageTextChanged = viewModel::onMessageTextChanged,
            onSendMessage = viewModel::sendMessage,
            validationResult = uiState.validationResult,
            isEnabled = uiState.isEnabled,
            error = uiState.error,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isFromUser
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Row(
        modifier = modifier,
        horizontalArrangement = if (isUserMessage) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUserMessage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUserMessage) 16.dp else 4.dp,
                bottomEnd = if (isUserMessage) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUserMessage) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = dateFormat.format(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUserMessage) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@Composable
fun TypingIndicator(
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
                text = "escribiendo...",
                style = MaterialTheme.typography.bodyMedium,
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
    validationResult: com.example.chatai.domain.usecase.MessageValidationResult,
    isEnabled: Boolean,
    error: String? = null,
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
            is com.example.chatai.domain.usecase.MessageValidationResult.TooLong -> {
                Text(
                    text = "El mensaje es muy largo (máximo 10,000 caracteres)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            else -> {}
        }
        
        // Character count
        Text(
            text = "${messageText.length}/10,000",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input field and send button
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
                isError = validationResult is com.example.chatai.domain.usecase.MessageValidationResult.TooLong
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = if (isEnabled && messageText.isNotBlank() && 
                             validationResult is com.example.chatai.domain.usecase.MessageValidationResult.Valid) {
                    onSendMessage
                } else {
                    {}
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar mensaje"
                )
            }
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
