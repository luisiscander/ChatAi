package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use Case para obtener estadísticas totales de uso de la aplicación
 */
class GetTotalUsageStatisticsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val getMessagesSyncUseCase: GetMessagesSyncUseCase
) {
    suspend operator fun invoke(): TotalUsageStatisticsResult {
        return try {
            val conversations = conversationRepository.getAllConversations().first()
            
            if (conversations.isEmpty()) {
                return TotalUsageStatisticsResult.Success(
                    TotalUsageStatistics(
                        totalConversations = 0,
                        totalMessages = 0,
                        totalTokens = 0,
                        totalCost = 0.0,
                        favoriteModel = "N/A",
                        modelBreakdown = emptyMap(),
                        usageByDay = emptyMap()
                    )
                )
            }
            
            var totalMessages = 0
            var totalTokens = 0
            var totalCost = 0.0
            val modelUsage = mutableMapOf<String, Int>()
            val dailyUsage = mutableMapOf<String, Int>()
            
            conversations.forEach { conversation ->
                val messages = getMessagesSyncUseCase(conversation.id)
                totalMessages += messages.size
                
                messages.forEach { message ->
                    // Accumulate token and cost data
                    message.totalTokens?.let { totalTokens += it }
                    message.estimatedCost?.let { totalCost += it }
                    
                    // Track model usage
                    if (!message.isFromUser && message.model != null) {
                        modelUsage[message.model] = modelUsage.getOrDefault(message.model, 0) + 1
                    }
                    
                    // Track daily usage (simplified - just counting messages per day)
                    val dateKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(message.timestamp)
                    dailyUsage[dateKey] = dailyUsage.getOrDefault(dateKey, 0) + 1
                }
            }
            
            val favoriteModel = modelUsage.maxByOrNull { it.value }?.key ?: "N/A"
            
            TotalUsageStatisticsResult.Success(
                TotalUsageStatistics(
                    totalConversations = conversations.size,
                    totalMessages = totalMessages,
                    totalTokens = totalTokens,
                    totalCost = totalCost,
                    favoriteModel = favoriteModel,
                    modelBreakdown = modelUsage,
                    usageByDay = dailyUsage
                )
            )
        } catch (e: Exception) {
            TotalUsageStatisticsResult.Error("Error al obtener estadísticas: ${e.message}")
        }
    }
}

data class TotalUsageStatistics(
    val totalConversations: Int,
    val totalMessages: Int,
    val totalTokens: Int,
    val totalCost: Double,
    val favoriteModel: String,
    val modelBreakdown: Map<String, Int>,
    val usageByDay: Map<String, Int>
)

sealed class TotalUsageStatisticsResult {
    data class Success(val statistics: TotalUsageStatistics) : TotalUsageStatisticsResult()
    data class Error(val message: String) : TotalUsageStatisticsResult()
}

