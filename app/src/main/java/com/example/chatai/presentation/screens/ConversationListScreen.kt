package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.R
import com.example.chatai.domain.model.ModelType
import com.example.chatai.ui.theme.ChatAiTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    onConversationClick: (String) -> Unit,
    onCreateConversation: (String) -> Unit,
    onShowArchived: () -> Unit,
    onNavigateToThemeSettings: () -> Unit = {},
    onNavigateToDefaultModelSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ConversationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isOnline by remember { mutableStateOf(true) }
    
    // Check network connectivity (Issue #116)
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = viewModel.checkNetworkConnection()
            kotlinx.coroutines.delay(5000) // Check every 5 seconds
        }
    }
    
    // Function to handle conversation creation
    val handleCreateConversation: () -> Unit = {
        coroutineScope.launch {
            val conversationId = viewModel.createConversation()
            conversationId?.let { id ->
                onCreateConversation(id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (showSearch) {
                        SearchBar(
                            searchText = searchText,
                            onSearchTextChanged = { text ->
                                searchText = text
                                viewModel.searchConversations(text)
                            },
                            onSearchClicked = { viewModel.searchConversations(searchText) },
                            onClearClicked = {
                                searchText = ""
                                viewModel.clearSearch()
                                showSearch = false
                            }
                        )
                    } else {
                        Text(stringResource(R.string.conversations))
                    }
                },
                actions = {
                    if (showSearch) {
                        IconButton(onClick = {
                            searchText = ""
                            viewModel.clearSearch()
                            showSearch = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.close_search))
                        }
                    } else {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                        }
                        IconButton(onClick = onShowArchived) {
                            Icon(Icons.Default.Star, contentDescription = stringResource(R.string.view_archived))
                        }
                        
                        // Settings menu
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.settings))
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.appearance_menu)) },
                                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                                    onClick = {
                                        onNavigateToThemeSettings()
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.default_model_menu)) },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    onClick = {
                                        onNavigateToDefaultModelSettings()
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = handleCreateConversation,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_conversation))
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Offline indicator (Issue #116)
            if (!isOnline) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sin conexión",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.searchResult != null -> {
                when (val result = uiState.searchResult) {
                    is com.example.chatai.domain.usecase.SearchResult.Success -> {
                        if (result.conversations.isEmpty()) {
                            EmptySearchResultState(
                                query = uiState.searchQuery,
                                modifier = Modifier.padding(paddingValues)
                            )
                        } else {
                            LazyColumn(
                                contentPadding = paddingValues,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(result.conversations) { conversation ->
                                    ConversationItem(
                                        conversation = conversation,
                                        onClick = { onConversationClick(conversation.id) },
                                        onArchive = { viewModel.archiveConversation(conversation.id) }
                                    )
                                }
                            }
                        }
                    }
                    is com.example.chatai.domain.usecase.SearchResult.NoResults -> {
                        EmptySearchResultState(
                            query = result.query,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    else -> {
                        EmptySearchResultState(
                            query = "",
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
            uiState.conversations.isEmpty() -> {
                EmptyConversationsState(
                    onCreateConversation = handleCreateConversation,
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
}

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onClearClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_conversations_placeholder)) },
        singleLine = true,
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = onClearClicked) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                }
            }
        }
    )
}

@Composable
fun EmptySearchResultState(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(R.string.no_results),
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_conversations_found),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.try_other_search_terms),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.you_searched, query),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
            contentDescription = stringResource(R.string.no_conversations),
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.no_conversations_message),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.start_new_conversation),
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
            Text(stringResource(R.string.create_conversation))
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
                Icon(Icons.Default.Star, contentDescription = stringResource(R.string.archive))
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
