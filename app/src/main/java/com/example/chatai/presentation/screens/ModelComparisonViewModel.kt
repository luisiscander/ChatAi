package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ComparisonMode
import com.example.chatai.domain.model.ModelComparison
import com.example.chatai.domain.model.ModelResponse
import com.example.chatai.domain.repository.UserPreferencesRepository
import com.example.chatai.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelComparisonViewModel @Inject constructor(
    private val manageComparisonModeUseCase: ManageComparisonModeUseCase,
    private val sendMessageToMultipleModelsUseCase: SendMessageToMultipleModelsUseCase,
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelComparisonUiState())
    val uiState: StateFlow<ModelComparisonUiState> = _uiState.asStateFlow()

    init {
        loadAvailableModels()
    }

    /**
     * Activa el modo de comparación
     */
    fun activateComparisonMode() {
        val mode = manageComparisonModeUseCase.activateComparisonMode()
        _uiState.value = _uiState.value.copy(
            comparisonMode = mode,
            showModelSelector = true
        )
    }

    /**
     * Desactiva el modo de comparación
     */
    fun deactivateComparisonMode() {
        val mode = manageComparisonModeUseCase.deactivateComparisonMode()
        _uiState.value = _uiState.value.copy(
            comparisonMode = mode,
            showModelSelector = false,
            currentComparison = null,
            modelResponses = emptyMap()
        )
    }

    /**
     * Agrega un modelo a la comparación
     */
    fun addModel(model: AiModel) {
        val result = manageComparisonModeUseCase.addModel(_uiState.value.comparisonMode, model)
        when (result) {
            is ComparisonModeResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    comparisonMode = result.mode,
                    error = null
                )
            }
            is ComparisonModeResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    error = result.message
                )
            }
        }
    }

    /**
     * Remueve un modelo de la comparación
     */
    fun removeModel(modelId: String) {
        val result = manageComparisonModeUseCase.removeModel(_uiState.value.comparisonMode, modelId)
        when (result) {
            is ComparisonModeResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    comparisonMode = result.mode,
                    error = null
                )
            }
            is ComparisonModeResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    error = result.message
                )
            }
        }
    }

    /**
     * Envía un mensaje a todos los modelos seleccionados
     */
    fun sendMessageToMultipleModels(conversationId: String, message: String) {
        viewModelScope.launch {
            try {
                // Validar que el modo comparación esté correcto
                val validationResult = manageComparisonModeUseCase.validateComparison(_uiState.value.comparisonMode)
                if (validationResult is ComparisonModeResult.Error) {
                    _uiState.value = _uiState.value.copy(error = validationResult.message)
                    return@launch
                }

                // Obtener API key
                val apiKey = userPreferencesRepository.getApiKey()
                if (apiKey.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(error = "API key no configurada")
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isSending = true,
                    error = null
                )

                val modelIds = _uiState.value.comparisonMode.selectedModels.map { it.id }

                // Iniciar comparación
                val comparison = ModelComparison(
                    conversationId = conversationId,
                    userMessage = message,
                    selectedModels = modelIds
                )
                _uiState.value = _uiState.value.copy(currentComparison = comparison)

                // Enviar mensaje a todos los modelos
                sendMessageToMultipleModelsUseCase(
                    conversationId = conversationId,
                    userMessage = message,
                    modelIds = modelIds,
                    apiKey = apiKey
                ).collect { chunk ->
                    when (chunk) {
                        is MultiModelResponseChunk.Started -> {
                            // Inicializar respuestas vacías para cada modelo
                            val responses = chunk.modelIds.associate { modelId ->
                                modelId to ModelResponse(
                                    modelId = modelId,
                                    modelName = getModelName(modelId),
                                    content = "",
                                    isStreaming = true
                                )
                            }
                            _uiState.value = _uiState.value.copy(modelResponses = responses)
                        }

                        is MultiModelResponseChunk.ModelStarted -> {
                            // Marcar modelo como iniciado
                            val currentResponses = _uiState.value.modelResponses.toMutableMap()
                            currentResponses[chunk.modelId] = ModelResponse(
                                modelId = chunk.modelId,
                                modelName = chunk.modelName,
                                content = "",
                                isStreaming = true
                            )
                            _uiState.value = _uiState.value.copy(modelResponses = currentResponses)
                        }

                        is MultiModelResponseChunk.ContentChunk -> {
                            // Actualizar contenido del modelo
                            val currentResponses = _uiState.value.modelResponses.toMutableMap()
                            val currentResponse = currentResponses[chunk.modelId]
                            if (currentResponse != null) {
                                currentResponses[chunk.modelId] = currentResponse.copy(
                                    content = chunk.accumulatedContent,
                                    streamingChunks = currentResponse.streamingChunks + chunk.chunk
                                )
                            }
                            _uiState.value = _uiState.value.copy(modelResponses = currentResponses)
                        }

                        is MultiModelResponseChunk.ModelComplete -> {
                            // Marcar modelo como completo
                            val currentResponses = _uiState.value.modelResponses.toMutableMap()
                            currentResponses[chunk.modelId] = chunk.response
                            _uiState.value = _uiState.value.copy(modelResponses = currentResponses)
                        }

                        is MultiModelResponseChunk.ModelError -> {
                            // Marcar error del modelo
                            val currentResponses = _uiState.value.modelResponses.toMutableMap()
                            currentResponses[chunk.modelId] = ModelResponse(
                                modelId = chunk.modelId,
                                modelName = getModelName(chunk.modelId),
                                content = "",
                                isComplete = true,
                                error = chunk.error
                            )
                            _uiState.value = _uiState.value.copy(modelResponses = currentResponses)
                        }

                        is MultiModelResponseChunk.AllComplete -> {
                            _uiState.value = _uiState.value.copy(
                                isSending = false,
                                totalResponseTime = chunk.totalTimeMs
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = e.message ?: "Error al enviar mensaje"
                )
            }
        }
    }

    /**
     * Marca una respuesta como la mejor
     */
    fun markAsBest(modelId: String) {
        val currentComparison = _uiState.value.currentComparison ?: return
        
        _uiState.value = _uiState.value.copy(
            currentComparison = currentComparison.copy(
                primaryResponseModelId = modelId
            ),
            selectedBestModelId = modelId
        )
    }

    /**
     * Carga los modelos disponibles
     */
    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                getAvailableModelsUseCase().collect { result ->
                    when (result) {
                        is GetModelsResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                availableModels = result.models
                            )
                        }
                        is GetModelsResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.message
                            )
                        }
                        is GetModelsResult.Loading -> {
                            // Optionally show loading state
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Obtiene el nombre de un modelo por su ID
     */
    private fun getModelName(modelId: String): String {
        return _uiState.value.availableModels
            .firstOrNull { it.id == modelId }?.name
            ?: modelId.split("/").lastOrNull()
            ?: modelId
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Muestra/oculta el selector de modelos
     */
    fun toggleModelSelector() {
        _uiState.value = _uiState.value.copy(
            showModelSelector = !_uiState.value.showModelSelector
        )
    }
}

data class ModelComparisonUiState(
    val comparisonMode: ComparisonMode = ComparisonMode(),
    val availableModels: List<AiModel> = emptyList(),
    val currentComparison: ModelComparison? = null,
    val modelResponses: Map<String, ModelResponse> = emptyMap(),
    val selectedBestModelId: String? = null,
    val isSending: Boolean = false,
    val totalResponseTime: Long? = null,
    val showModelSelector: Boolean = false,
    val error: String? = null
)

