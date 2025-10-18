package com.example.chatai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.ui.theme.ChatAiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupScreen(
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ApiKeySetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Configurar API Key",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val uriHandler = LocalUriHandler.current
        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            ) {
                append("Necesitas una API key de OpenRouter para usar la aplicación. ")
            }
            
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            ) {
                append("Puedes obtenerla aquí")
            }
        }
        
        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center
            ),
            onClick = { offset ->
                // Abrir el enlace a OpenRouter
                uriHandler.openUri("https://openrouter.ai/keys")
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = uiState.apiKey,
            onValueChange = { 
                viewModel.onApiKeyChanged(it)
                viewModel.clearMessages()
            },
            label = { Text("API Key") },
            placeholder = { Text("sk-or-v1-...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.errorMessage != null
        )
        
        // Mostrar mensajes de error, éxito o validación
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        uiState.successMessage?.let { success ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = success,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        uiState.validationMessage?.let { validation ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validation,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón de validar
        if (uiState.apiKey.isNotBlank()) {
            OutlinedButton(
                onClick = { viewModel.validateApiKey() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Validar API Key")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Botón de guardar
        Button(
            onClick = { viewModel.saveApiKey(onApiKeyConfigured) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = uiState.apiKey.isNotBlank() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Guardar API Key",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApiKeySetupScreenPreview() {
    ChatAiTheme {
        ApiKeySetupScreen(
            onApiKeyConfigured = { }
        )
    }
}
