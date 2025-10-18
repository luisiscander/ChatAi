package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.ApiKeyValidationResult
import com.example.chatai.domain.usecase.SaveApiKeyResult
import com.example.chatai.domain.usecase.SaveApiKeyUseCase
import com.example.chatai.domain.usecase.ValidateApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeySetupViewModel @Inject constructor(
    private val validateApiKeyUseCase: ValidateApiKeyUseCase,
    private val saveApiKeyUseCase: SaveApiKeyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiKeySetupUiState())
    val uiState: StateFlow<ApiKeySetupUiState> = _uiState.asStateFlow()

    fun onApiKeyChanged(apiKey: String) {
        _uiState.value = _uiState.value.copy(
            apiKey = apiKey,
            errorMessage = null,
            isLoading = false
        )
    }

    fun validateApiKey() {
        val apiKey = _uiState.value.apiKey
        if (apiKey.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor ingresa tu API key",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val validationResult = validateApiKeyUseCase(apiKey)
            
            _uiState.value = when (validationResult) {
                is ApiKeyValidationResult.Empty -> {
                    _uiState.value.copy(
                        errorMessage = "Por favor ingresa tu API key",
                        isLoading = false
                    )
                }
                is ApiKeyValidationResult.InvalidFormat -> {
                    _uiState.value.copy(
                        errorMessage = "Formato de API key inválido",
                        isLoading = false
                    )
                }
                is ApiKeyValidationResult.Valid -> {
                    _uiState.value.copy(
                        validationMessage = "API key válida ✓",
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun saveApiKey(onSuccess: () -> Unit) {
        val apiKey = _uiState.value.apiKey
        if (apiKey.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor ingresa tu API key",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val validationResult = validateApiKeyUseCase(apiKey)
            
            when (validationResult) {
                is ApiKeyValidationResult.Empty -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Por favor ingresa tu API key",
                        isLoading = false
                    )
                }
                is ApiKeyValidationResult.InvalidFormat -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Formato de API key inválido",
                        isLoading = false
                    )
                }
                is ApiKeyValidationResult.Valid -> {
                    val saveResult = saveApiKeyUseCase(apiKey)
                    _uiState.value = when (saveResult) {
                        is SaveApiKeyResult.Success -> {
                            _uiState.value.copy(
                                successMessage = "API key configurada correctamente",
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is SaveApiKeyResult.Error -> {
                            _uiState.value.copy(
                                errorMessage = saveResult.message,
                                isLoading = false
                            )
                        }
                    }
                    
                    if (saveResult is SaveApiKeyResult.Success) {
                        onSuccess()
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            validationMessage = null
        )
    }
}

data class ApiKeySetupUiState(
    val apiKey: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val validationMessage: String? = null
)
