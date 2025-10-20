package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.DeleteAllDataUseCase
import com.example.chatai.domain.usecase.DeleteAllDataResult
import com.example.chatai.domain.usecase.ExportAllConversationsUseCase
import com.example.chatai.domain.usecase.ExportAllResult
import com.example.chatai.domain.usecase.ExportFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
    private val exportAllConversationsUseCase: ExportAllConversationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySettingsUiState())
    val uiState: StateFlow<PrivacySettingsUiState> = _uiState.asStateFlow()

    fun showDeleteConfirmationDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            deleteConfirmationText = ""
        )
    }

    fun hideDeleteConfirmationDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            deleteConfirmationText = "",
            deleteError = null
        )
    }

    fun updateDeleteConfirmationText(text: String) {
        _uiState.value = _uiState.value.copy(
            deleteConfirmationText = text,
            deleteError = null
        )
    }

    fun deleteAllData() {
        val confirmationText = _uiState.value.deleteConfirmationText
        
        // Validate confirmation text (Issue #106)
        if (confirmationText != "ELIMINAR") {
            _uiState.value = _uiState.value.copy(
                deleteError = "Escribe ELIMINAR (en mayúsculas) para confirmar"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, deleteError = null)
            
            when (val result = deleteAllDataUseCase()) {
                is DeleteAllDataResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        deleteSuccess = true,
                        showDeleteDialog = false
                    )
                }
                is DeleteAllDataResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        deleteError = result.message
                    )
                }
            }
        }
    }

    fun exportAllConversations(format: ExportFormat) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExporting = true,
                exportError = null
            )
            
            when (val result = exportAllConversationsUseCase(format)) {
                is ExportAllResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportResult = result
                    )
                }
                is ExportAllResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportError = result.message
                    )
                }
            }
        }
    }

    fun clearExportResult() {
        _uiState.value = _uiState.value.copy(
            exportResult = null,
            exportError = null
        )
    }
}

data class PrivacySettingsUiState(
    val showDeleteDialog: Boolean = false,
    val deleteConfirmationText: String = "",
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null,
    val isExporting: Boolean = false,
    val exportResult: ExportAllResult.Success? = null,
    val exportError: String? = null
)

