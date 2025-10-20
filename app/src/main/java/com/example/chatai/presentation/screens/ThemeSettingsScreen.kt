package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.ui.theme.ChatAiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ThemeSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apariencia") },
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
            Text(
                text = "Selecciona el tema de la aplicación",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Theme selection
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.values().forEach { themeMode ->
                    ThemeOption(
                        themeMode = themeMode,
                        isSelected = uiState.selectedThemeMode == themeMode,
                        onThemeSelected = { viewModel.selectTheme(themeMode) }
                    )
                }
            }
            
            // Preview section
            if (uiState.showPreview) {
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
                            text = "Vista previa",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Este es un ejemplo de cómo se verá la aplicación con el tema seleccionado.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* Preview action */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ejemplo de botón")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.cancelPreview() },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.showPreview
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = { viewModel.applyTheme() },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.hasChanges
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    themeMode: ThemeMode,
    isSelected: Boolean,
    onThemeSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onThemeSelected,
                role = Role.RadioButton
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = themeMode.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = getThemeDescription(themeMode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getThemeDescription(themeMode: ThemeMode): String {
    return when (themeMode) {
        ThemeMode.LIGHT -> "Usa colores claros independientemente de la configuración del sistema"
        ThemeMode.DARK -> "Usa colores oscuros independientemente de la configuración del sistema"
        ThemeMode.SYSTEM -> "Sigue automáticamente la configuración del sistema operativo"
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSettingsScreenPreview() {
    ChatAiTheme {
        ThemeSettingsScreen(
            onBackClicked = { }
        )
    }
}
