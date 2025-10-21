package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Use Case para obtener estadísticas de una conversación
 */
class GetConversationStatisticsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val getMessagesUseCase: GetMessagesUseCase
) {
    suspend operator fun invoke(conversationId: String): ConversationStatisticsResult {
        return try {
            val messages = getMessagesUseCase(conversationId).first()
            
            if (messages.isEmpty()) {
                return ConversationStatisticsResult.Success(
                    ConversationStatistics(
                        totalMessages = 0,
                        totalTokens = 0,
                        totalCost = 0.0,
                        mostUsedModel = "N/A",
                        chatDuration = "0 min"
                    )
                )
            }
            
            val totalMessages = messages.size
            val totalTokens = messages.mapNotNull { it.totalTokens }.sum()
            val totalCost = messages.mapNotNull { it.estimatedCost }.sum()
            
            // Find most used model
            val modelCounts = messages
                .filter { !it.isFromUser && it.model != null }
                .groupingBy { it.model!! }
                .eachCount()
            val mostUsedModel = modelCounts.maxByOrNull { it.value }?.key ?: "N/A"
            
            // Calculate chat duration
            val firstMessage = messages.minByOrNull { it.timestamp }
            val lastMessage = messages.maxByOrNull { it.timestamp }
            val durationMs = if (firstMessage != null && lastMessage != null) {
                lastMessage.timestamp.time - firstMessage.timestamp.time
            } else {
                0L
            }
            val chatDuration = formatDuration(durationMs)
            
            ConversationStatisticsResult.Success(
                ConversationStatistics(
                    totalMessages = totalMessages,
                    totalTokens = totalTokens,
                    totalCost = totalCost,
                    mostUsedModel = mostUsedModel,
                    chatDuration = chatDuration
                )
            )
        } catch (e: Exception) {
            ConversationStatisticsResult.Error("Error al obtener estadísticas: ${e.message}")
        }
    }
    
    private fun formatDuration(millis: Long): String {
        val minutes = millis / (1000 * 60)
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "$hours h ${minutes % 60} min"
            minutes > 0 -> "$minutes min"
            else -> "< 1 min"
        }
    }
}

data class ConversationStatistics(
    val totalMessages: Int,
    val totalTokens: Int,
    val totalCost: Double,
    val mostUsedModel: String,
    val chatDuration: String
)

sealed class ConversationStatisticsResult {
    data class Success(val statistics: ConversationStatistics) : ConversationStatisticsResult()
    data class Error(val message: String) : ConversationStatisticsResult()
}

