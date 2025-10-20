package com.example.chatai.presentation.screens

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.usecase.ExportConversationUseCase
import com.example.chatai.domain.usecase.ExportFormat
import com.example.chatai.domain.usecase.ShareFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportConversationViewModel @Inject constructor(
    private val exportConversationUseCase: ExportConversationUseCase,
    private val shareFileUseCase: ShareFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportConversationUiState())
    val uiState: StateFlow<ExportConversationUiState> = _uiState.asStateFlow()

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                conversationId = conversationId,
                conversationTitle = "Conversación ${conversationId.takeLast(8)}"
            )
        }
    }

    fun selectFormat(format: ExportFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }

    fun exportConversation(): Intent? {
        val conversationId = _uiState.value.conversationId
        val format = _uiState.value.selectedFormat
        
        if (conversationId != null && format != null) {
            _uiState.value = _uiState.value.copy(isExporting = true, errorMessage = null)
            
            viewModelScope.launch {
                try {
                    val exportResult = exportConversationUseCase(conversationId, format)
                    
                    when (exportResult) {
                        is com.example.chatai.domain.usecase.ExportResult.Success -> {
                            val fileName = "conversacion.${format.fileExtension}"
                            val mimeType = getMimeType(format)
                            
                            val shareIntent = shareFileUseCase.shareTextFile(
                                content = exportResult.content,
                                fileName = fileName,
                                mimeType = mimeType
                            )
                            
                            _uiState.value = _uiState.value.copy(
                                isExporting = false,
                                shareIntent = shareIntent
                            )
                        }
                        is com.example.chatai.domain.usecase.ExportResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isExporting = false,
                                errorMessage = exportResult.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        errorMessage = "Error al exportar: ${e.message}"
                    )
                }
            }
            
            return _uiState.value.shareIntent
        }
        
        return null
    }

    fun onExportCompleted() {
        _uiState.value = _uiState.value.copy(shareIntent = null)
    }
    
    private fun getMimeType(format: ExportFormat): String {
        return when (format) {
            ExportFormat.TEXT -> "text/plain"
            ExportFormat.MARKDOWN -> "text/markdown"
            ExportFormat.JSON -> "application/json"
        }
    }
}

data class ExportConversationUiState(
    val conversationId: String? = null,
    val conversationTitle: String? = null,
    val selectedFormat: ExportFormat? = null,
    val isExporting: Boolean = false,
    val errorMessage: String? = null,
    val shareIntent: Intent? = null
)
