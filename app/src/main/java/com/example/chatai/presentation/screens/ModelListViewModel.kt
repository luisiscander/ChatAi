package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.GetAvailableModelsUseCase
import com.example.chatai.domain.usecase.GetModelsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelListViewModel @Inject constructor(
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
    private val refreshModelsUseCase: com.example.chatai.domain.usecase.RefreshModelsUseCase // Issue #130
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelListUiState())
    val uiState: StateFlow<ModelListUiState> = _uiState.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(result = GetModelsResult.Loading)
            
            try {
                val result = getAvailableModelsUseCase().first()
                _uiState.value = _uiState.value.copy(result = result)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    result = GetModelsResult.Error(exception.message ?: "Error al cargar modelos")
                )
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

    // Issue #130: Pull-to-refresh implementation
    fun refreshModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            try {
                val refreshResult = refreshModelsUseCase()
                when (refreshResult) {
                    is com.example.chatai.domain.usecase.RefreshModelsResult.Success -> {
                        // Reload models after successful refresh
                        loadModels()
                    }
                    is com.example.chatai.domain.usecase.RefreshModelsResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            result = GetModelsResult.Error(refreshResult.message),
                            isRefreshing = false
                        )
                    }
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    result = GetModelsResult.Error(exception.message ?: "Error al actualizar"),
                    isRefreshing = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}

data class ModelListUiState(
    val result: GetModelsResult = GetModelsResult.Loading,
    val isRefreshing: Boolean = false // Issue #130: Pull-to-refresh state
)
