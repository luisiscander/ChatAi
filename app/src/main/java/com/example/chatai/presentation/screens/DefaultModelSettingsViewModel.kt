package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.usecase.GetAvailableModelsUseCase
import com.example.chatai.domain.usecase.GetDefaultModelUseCase
import com.example.chatai.domain.usecase.GetModelsResult
import com.example.chatai.domain.usecase.SetDefaultModelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultModelSettingsViewModel @Inject constructor(
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
    private val getDefaultModelUseCase: GetDefaultModelUseCase,
    private val setDefaultModelUseCase: SetDefaultModelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DefaultModelSettingsUiState())
    val uiState: StateFlow<DefaultModelSettingsUiState> = _uiState.asStateFlow()

    fun loadModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val modelsResult = getAvailableModelsUseCase()
                val currentDefaultModel = getDefaultModelUseCase()
                
                val result = modelsResult.first()
                when (result) {
                    is GetModelsResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            availableModels = result.models,
                            selectedModelId = currentDefaultModel,
                            originalSelectedModelId = currentDefaultModel,
                            isLoading = false
                        )
                    }
                    is GetModelsResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar modelos: ${result.message}"
                        )
                    }
                    is GetModelsResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar modelos: ${e.message}"
                )
            }
        }
    }

    fun selectModel(modelId: String) {
        _uiState.value = _uiState.value.copy(
            selectedModelId = modelId,
            hasChanges = _uiState.value.originalSelectedModelId != modelId
        )
    }

    fun saveDefaultModel() {
        val selectedModelId = _uiState.value.selectedModelId
        if (selectedModelId != null) {
            viewModelScope.launch {
                try {
                    setDefaultModelUseCase(selectedModelId)
                    _uiState.value = _uiState.value.copy(
                        originalSelectedModelId = selectedModelId,
                        hasChanges = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al guardar modelo por defecto: ${e.message}"
                    )
                }
            }
        }
    }
}

data class DefaultModelSettingsUiState(
    val availableModels: List<AiModel> = emptyList(),
    val selectedModelId: String? = null,
    val originalSelectedModelId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasChanges: Boolean = false
)
