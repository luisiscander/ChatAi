package com.example.chatai.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.R
import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ModelResponse
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelComparisonScreen(
    conversationId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.activateComparisonMode()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.comparison_mode)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.deactivateComparisonMode()
                        onBackClicked()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selected Models Bar
            SelectedModelsBar(
                selectedModels = uiState.comparisonMode.selectedModels,
                totalCost = uiState.comparisonMode.totalEstimatedCost,
                onRemoveModel = { viewModel.removeModel(it.id) },
                onAddModel = { viewModel.toggleModelSelector() },
                maxModels = uiState.comparisonMode.maxModels
            )

            // Model Selector Dialog
            if (uiState.showModelSelector) {
                ModelSelectorDialog(
                    availableModels = uiState.availableModels,
                    selectedModels = uiState.comparisonMode.selectedModels,
                    onModelSelected = { viewModel.addModel(it) },
                    onDismiss = { viewModel.toggleModelSelector() }
                )
            }

            // Responses Section
            if (uiState.modelResponses.isNotEmpty()) {
                ModelResponsesGrid(
                    responses = uiState.modelResponses,
                    selectedBestId = uiState.selectedBestModelId,
                    onMarkAsBest = { viewModel.markAsBest(it) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                EmptyComparisonState(
                    modifier = Modifier.weight(1f)
                )
            }

            // Input Section
            MessageInputSection(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessageToMultipleModels(conversationId, messageText)
                        messageText = ""
                    }
                },
                enabled = uiState.comparisonMode.selectedModels.size >= 2 && !uiState.isSending,
                isSending = uiState.isSending
            )

            // Error message
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun SelectedModelsBar(
    selectedModels: List<AiModel>,
    totalCost: Double,
    onRemoveModel: (AiModel) -> Unit,
    onAddModel: () -> Unit,
    maxModels: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.selected_models_count, selectedModels.size, maxModels),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = stringResource(R.string.estimated_cost_format, formatCost(totalCost)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedModels) { model ->
                    SelectedModelChip(
                        model = model,
                        onRemove = { onRemoveModel(model) }
                    )
                }

                if (selectedModels.size < maxModels) {
                    item {
                        AssistChip(
                            onClick = onAddModel,
                            label = { Text(stringResource(R.string.add_model)) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedModelChip(
    model: AiModel,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    InputChip(
        selected = true,
        onClick = onRemove,
        label = { Text(model.name) },
        trailingIcon = {
            Icon(
                Icons.Default.Clear,
                contentDescription = stringResource(R.string.remove),
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
fun ModelSelectorDialog(
    availableModels: List<AiModel>,
    selectedModels: List<AiModel>,
    onModelSelected: (AiModel) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_models)) },
        text = {
            LazyColumn {
                items(availableModels) { model ->
                    val isSelected = selectedModels.any { it.id == model.id }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = model.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = model.id,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Button(
                                onClick = { onModelSelected(model) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(stringResource(R.string.select))
                            }
                        }
                    }
                    Divider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.done))
            }
        }
    )
}

@Composable
fun ModelResponsesGrid(
    responses: Map<String, ModelResponse>,
    selectedBestId: String?,
    onMarkAsBest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(responses.entries.toList()) { entry ->
            ModelResponseCard(
                response = entry.value,
                isBest = entry.key == selectedBestId,
                onMarkAsBest = { onMarkAsBest(entry.key) },
                modifier = Modifier.width(300.dp)
            )
        }
    }
}

@Composable
fun ModelResponseCard(
    response: ModelResponse,
    isBest: Boolean,
    onMarkAsBest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .then(
                if (isBest) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBest) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header with model name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = response.modelName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isBest) {
                        Text(
                            text = stringResource(R.string.best_response),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onMarkAsBest,
                    enabled = response.isComplete && response.error == null
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = stringResource(R.string.mark_as_best),
                        tint = if (isBest) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Response content
            Box(modifier = Modifier.weight(1f)) {
                if (response.error != null) {
                    Text(
                        text = stringResource(R.string.error_format, response.error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState) {
                        item {
                            Text(
                                text = response.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (response.isStreaming) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            // Statistics
            if (response.isComplete && response.error == null) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    response.responseTimeMs?.let {
                        StatRow(stringResource(R.string.response_time), "${it}ms")
                    }
                    response.tokensUsed?.let {
                        StatRow(stringResource(R.string.tokens_used), it.toString())
                    }
                    response.estimatedCost?.let {
                        StatRow(stringResource(R.string.cost), formatCost(it))
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MessageInputSection(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = { Text(stringResource(R.string.type_message_to_compare)) },
                modifier = Modifier.weight(1f),
                enabled = enabled && !isSending,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = enabled && messageText.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = stringResource(R.string.send))
                }
            }
        }
    }
}

@Composable
fun EmptyComparisonState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.select_models_and_send_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.minimum_2_models_required),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatCost(cost: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(cost)
}

