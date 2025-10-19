package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.model.ModelType
import com.example.chatai.ui.theme.ChatAiTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    onConversationClick: (String) -> Unit,
    onCreateConversation: () -> Unit,
    onShowArchived: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversaciones") },
                actions = {
                    IconButton(onClick = onShowArchived) {
                        Icon(Icons.Default.Star, contentDescription = "Ver archivadas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateConversation,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva conversación")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.conversations.isEmpty() -> {
                EmptyConversationsState(
                    onCreateConversation = onCreateConversation,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.conversations) { conversation ->
                        ConversationItem(
                            conversation = conversation,
                            onClick = { onConversationClick(conversation.id) },
                            onArchive = { viewModel.archiveConversation(conversation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyConversationsState(
    onCreateConversation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Sin conversaciones",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No tienes conversaciones",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Comienza una nueva conversación con IA",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onCreateConversation,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear conversación")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationItem(
    conversation: com.example.chatai.domain.model.Conversation,
    onClick: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modelType = ModelType.fromModelId(conversation.model)
    val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del modelo
            Icon(
                imageVector = getModelIcon(modelType),
                contentDescription = modelType.displayName,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Contenido de la conversación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = modelType.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                conversation.lastMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = dateFormat.format(conversation.lastActivity),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Botón de archivar
            IconButton(onClick = onArchive) {
                Icon(Icons.Default.Star, contentDescription = "Archivar")
            }
        }
    }
}

@Composable
fun getModelIcon(modelType: ModelType): ImageVector {
    // Simplified - using Star icon for all models for now
    return Icons.Default.Star
}

@Preview(showBackground = true)
@Composable
fun ConversationListScreenPreview() {
    ChatAiTheme {
        ConversationListScreen(
            onConversationClick = { },
            onCreateConversation = { },
            onShowArchived = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyConversationsStatePreview() {
    ChatAiTheme {
        EmptyConversationsState(
            onCreateConversation = { }
        )
    }
}
