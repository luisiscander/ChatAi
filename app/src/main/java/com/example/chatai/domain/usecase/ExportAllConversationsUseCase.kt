package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportAllConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val exportConversationUseCase: ExportConversationUseCase
) {
    suspend operator fun invoke(format: ExportFormat): ExportAllResult {
        return try {
            // Get all conversations
            val conversations = conversationRepository.getAllConversations().first()
            
            if (conversations.isEmpty()) {
                return ExportAllResult.Error("No hay conversaciones para exportar")
            }
            
            val exports = mutableListOf<ConversationExport>()
            var successCount = 0
            var failureCount = 0
            
            // Export each conversation
            conversations.forEach { conversation ->
                when (val result = exportConversationUseCase(conversation.id, format)) {
                    is ExportResult.Success -> {
                        exports.add(
                            ConversationExport(
                                conversationId = conversation.id,
                                title = conversation.title,
                                content = result.content,
                                fileName = sanitizeFileName("${conversation.title}.${format.fileExtension}")
                            )
                        )
                        successCount++
                    }
                    is ExportResult.Error -> {
                        failureCount++
                    }
                }
            }
            
            if (exports.isEmpty()) {
                ExportAllResult.Error("No se pudo exportar ninguna conversación")
            } else {
                ExportAllResult.Success(
                    exports = exports,
                    totalCount = conversations.size,
                    successCount = successCount,
                    failureCount = failureCount
                )
            }
        } catch (e: Exception) {
            ExportAllResult.Error(e.message ?: "Error al exportar conversaciones")
        }
    }
    
    private fun sanitizeFileName(fileName: String): String {
        // Remove invalid characters for file names
        return fileName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
    }
}

data class ConversationExport(
    val conversationId: String,
    val title: String,
    val content: String,
    val fileName: String
)

sealed class ExportAllResult {
    data class Success(
        val exports: List<ConversationExport>,
        val totalCount: Int,
        val successCount: Int,
        val failureCount: Int
    ) : ExportAllResult()
    data class Error(val message: String) : ExportAllResult()
}

