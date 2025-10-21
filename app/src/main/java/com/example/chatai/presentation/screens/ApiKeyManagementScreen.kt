package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatai.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyManagementScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    userPreferencesRepository: UserPreferencesRepository
) {
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasExistingKey by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Verificar si existe API key al cargar
    LaunchedEffect(Unit) {
        hasExistingKey = userPreferencesRepository.hasApiKey()
        if (hasExistingKey) {
            // Obtener la API key actual (para mostrar que existe, no el valor)
            val currentKey = userPreferencesRepository.getApiKey()
            if (currentKey != null) {
                apiKey = currentKey
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar API Key") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                text = "Configura tu API Key de OpenRouter",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Tu API key se utiliza para acceder a los modelos de IA. Consigue una en openrouter.ai",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // API Key Input
            OutlinedTextField(
                value = apiKey,
                onValueChange = { 
                    apiKey = it
                    errorMessage = null
                    successMessage = null
                },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("sk-or-v1-...") },
                singleLine = true,
                enabled = !isLoading,
                supportingText = {
                    if (hasExistingKey) {
                        Text("Ya tienes una API key configurada")
                    }
                }
            )
            
            // Success/Error messages
            successMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        if (apiKey.isBlank()) {
                            errorMessage = "Por favor ingresa una API key"
                            return@launch
                        }
                        
                        isLoading = true
                        try {
                            userPreferencesRepository.setApiKey(apiKey)
                            successMessage = "✓ API Key guardada correctamente"
                            errorMessage = null
                            hasExistingKey = true
                        } catch (e: Exception) {
                            errorMessage = "Error al guardar: ${e.message}"
                            successMessage = null
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && apiKey.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (hasExistingKey) "Actualizar API Key" else "Guardar API Key")
                }
            }
            
            // Clear Button (solo si existe una key)
            if (hasExistingKey) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                userPreferencesRepository.clearApiKey()
                                apiKey = ""
                                successMessage = "✓ API Key eliminada"
                                errorMessage = null
                                hasExistingKey = false
                            } catch (e: Exception) {
                                errorMessage = "Error al eliminar: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Eliminar API Key")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ℹ️ Información",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "• Obtén tu API key en https://openrouter.ai/keys",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• La API key debe empezar con 'sk-or-v1-'",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Los modelos gratuitos de Gemini no requieren crédito",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• NOTA: Temporalmente guardada sin encriptar para debugging",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

