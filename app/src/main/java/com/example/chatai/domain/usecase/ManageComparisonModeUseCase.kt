package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ComparisonMode
import javax.inject.Inject

/**
 * Use Case para gestionar el modo de comparación de modelos
 */
class ManageComparisonModeUseCase @Inject constructor() {
    
    /**
     * Activa el modo comparación
     */
    fun activateComparisonMode(): ComparisonMode {
        return ComparisonMode(isActive = true)
    }
    
    /**
     * Desactiva el modo comparación
     */
    fun deactivateComparisonMode(): ComparisonMode {
        return ComparisonMode(isActive = false, selectedModels = emptyList())
    }
    
    /**
     * Agrega un modelo a la comparación
     */
    fun addModel(currentMode: ComparisonMode, model: AiModel): ComparisonModeResult {
        if (currentMode.selectedModels.size >= currentMode.maxModels) {
            return ComparisonModeResult.Error("Máximo ${currentMode.maxModels} modelos permitidos")
        }
        
        if (currentMode.selectedModels.any { it.id == model.id }) {
            return ComparisonModeResult.Error("El modelo ${model.name} ya está seleccionado")
        }
        
        val updatedModels = currentMode.selectedModels + model
        val estimatedCost = calculateEstimatedCost(updatedModels)
        
        return ComparisonModeResult.Success(
            currentMode.copy(
                selectedModels = updatedModels,
                totalEstimatedCost = estimatedCost
            )
        )
    }
    
    /**
     * Remueve un modelo de la comparación
     */
    fun removeModel(currentMode: ComparisonMode, modelId: String): ComparisonModeResult {
        val updatedModels = currentMode.selectedModels.filter { it.id != modelId }
        val estimatedCost = calculateEstimatedCost(updatedModels)
        
        return ComparisonModeResult.Success(
            currentMode.copy(
                selectedModels = updatedModels,
                totalEstimatedCost = estimatedCost
            )
        )
    }
    
    /**
     * Calcula el costo estimado total basado en los modelos seleccionados
     */
    private fun calculateEstimatedCost(models: List<AiModel>): Double {
        // Costo estimado por 1000 tokens (promedio de input/output)
        // Estos son valores aproximados, en producción deberían venir de la API
        val modelPrices = mapOf(
            "gpt-4" to 0.03,
            "gpt-4-turbo" to 0.01,
            "gpt-3.5-turbo" to 0.002,
            "claude-3-opus" to 0.015,
            "claude-3-sonnet" to 0.003,
            "llama-3-70b" to 0.001
        )
        
        // Asumiendo un mensaje promedio de 500 tokens
        val estimatedTokens = 500.0
        return models.sumOf { model ->
            val baseModelId = model.id.split("/").lastOrNull() ?: model.id
            val pricePerK = modelPrices.entries.firstOrNull { 
                baseModelId.contains(it.key, ignoreCase = true) 
            }?.value ?: 0.01 // Default price
            
            (estimatedTokens / 1000.0) * pricePerK
        }
    }
    
    /**
     * Valida que se puedan enviar mensajes en modo comparación
     */
    fun validateComparison(mode: ComparisonMode): ComparisonModeResult {
        return when {
            !mode.isActive -> ComparisonModeResult.Error("Modo comparación no está activo")
            mode.selectedModels.isEmpty() -> ComparisonModeResult.Error("Selecciona al menos un modelo")
            mode.selectedModels.size < 2 -> ComparisonModeResult.Error("Selecciona al menos 2 modelos para comparar")
            else -> ComparisonModeResult.Success(mode)
        }
    }
}

sealed class ComparisonModeResult {
    data class Success(val mode: ComparisonMode) : ComparisonModeResult()
    data class Error(val message: String) : ComparisonModeResult()
}

