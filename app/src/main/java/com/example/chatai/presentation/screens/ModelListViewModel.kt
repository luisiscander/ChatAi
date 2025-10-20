package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.GetAvailableModelsUseCase
import com.example.chatai.domain.usecase.GetModelsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelListViewModel @Inject constructor(
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelListUiState())
    val uiState: StateFlow<ModelListUiState> = _uiState.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(result = GetModelsResult.Loading)
            
            getAvailableModelsUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        result = GetModelsResult.Error(exception.message ?: "Error al cargar modelos")
                    )
                }
                .collect { result ->
                    _uiState.value = _uiState.value.copy(result = result)
                }
        }
    }

    fun searchModels(query: String) {
        // For now, we'll implement basic filtering in the UI
        // In a real implementation, this would call the repository
        viewModelScope.launch {
            // TODO: Implement search functionality
        }
    }

    fun refreshModels() {
        loadModels()
    }
}

data class ModelListUiState(
    val result: GetModelsResult = GetModelsResult.Loading
)
