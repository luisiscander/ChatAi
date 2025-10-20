package com.example.chatai.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.usecase.ExportFormat
import com.example.chatai.ui.theme.ChatAiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportConversationScreen(
    conversationId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExportConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }
    
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle result if needed
        viewModel.onExportCompleted()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exportar conversación") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Conversation info
            if (uiState.conversationTitle != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Conversación",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = uiState.conversationTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Text(
                text = "Selecciona el formato de exportación",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Format selection
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportFormat.values().forEach { format ->
                    FormatOption(
                        format = format,
                        isSelected = uiState.selectedFormat == format,
                        onFormatSelected = { viewModel.selectFormat(format) }
                    )
                }
            }
            
            // Format descriptions
            uiState.selectedFormat?.let { format ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción del formato",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getFormatDescription(format),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Export button
            Button(
                onClick = {
                    val shareIntent = viewModel.exportConversation()
                    if (shareIntent != null) {
                        shareLauncher.launch(shareIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.selectedFormat != null && !uiState.isExporting,
                contentPadding = PaddingValues(16.dp)
            ) {
                if (uiState.isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Share, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar y compartir")
            }
            
            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun FormatOption(
    format: ExportFormat,
    isSelected: Boolean,
    onFormatSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onFormatSelected,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Archivo .${format.fileExtension}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getFormatDescription(format: ExportFormat): String {
    return when (format) {
        ExportFormat.TEXT -> "Formato de texto plano simple. Incluye todos los mensajes con timestamps y información básica de la conversación."
        ExportFormat.MARKDOWN -> "Formato Markdown con encabezados, formato de texto y bloques de código. Ideal para documentación o compartir en plataformas que soporten Markdown."
        ExportFormat.JSON -> "Formato JSON estructurado con toda la información de la conversación. Útil para análisis de datos o integración con otras aplicaciones."
    }
}

@Preview(showBackground = true)
@Composable
fun ExportConversationScreenPreview() {
    ChatAiTheme {
        ExportConversationScreen(
            conversationId = "test",
            onBackClicked = { }
        )
    }
}
