package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        format: ExportFormat
    ): ExportResult {
        return try {
            val conversation = conversationRepository.getConversationById(conversationId)
            val messages = conversationRepository.getMessages(conversationId)
            
            val exportedContent = when (format) {
                ExportFormat.TEXT -> exportAsText(conversation, messages)
                ExportFormat.MARKDOWN -> exportAsMarkdown(conversation, messages)
                ExportFormat.JSON -> exportAsJson(conversation, messages)
            }
            
            ExportResult.Success(exportedContent)
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Error al exportar conversación")
        }
    }
    
    private fun exportAsText(conversation: Conversation, messages: List<Message>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        
        sb.appendLine("Conversación: ${conversation.title}")
        sb.appendLine("Fecha: ${dateFormat.format(Date(conversation.createdAt))}")
        sb.appendLine("Modelo: ${conversation.modelId}")
        sb.appendLine("=" * 50)
        sb.appendLine()
        
        messages.forEach { message ->
            val sender = if (message.isFromUser) "Usuario" else "Asistente"
            val timestamp = dateFormat.format(Date(message.timestamp))
            
            sb.appendLine("[$timestamp] $sender:")
            sb.appendLine(message.content)
            sb.appendLine()
        }
        
        return sb.toString()
    }
    
    private fun exportAsMarkdown(conversation: Conversation, messages: List<Message>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        
        sb.appendLine("# ${conversation.title}")
        sb.appendLine()
        sb.appendLine("**Fecha:** ${dateFormat.format(Date(conversation.createdAt))}")
        sb.appendLine("**Modelo:** ${conversation.modelId}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        
        messages.forEach { message ->
            val sender = if (message.isFromUser) "👤 Usuario" else "🤖 Asistente"
            val timestamp = dateFormat.format(Date(message.timestamp))
            
            sb.appendLine("## $sender")
            sb.appendLine("*$timestamp*")
            sb.appendLine()
            
            // Format code blocks if present
            val formattedContent = formatMarkdownContent(message.content)
            sb.appendLine(formattedContent)
            sb.appendLine()
        }
        
        return sb.toString()
    }
    
    private fun exportAsJson(conversation: Conversation, messages: List<Message>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        
        val json = """
        {
            "conversation": {
                "id": "${conversation.id}",
                "title": "${conversation.title}",
                "createdAt": "${dateFormat.format(Date(conversation.createdAt))}",
                "modelId": "${conversation.modelId}",
                "isArchived": ${conversation.isArchived}
            },
            "messages": [
                ${messages.joinToString(",\n                ") { message ->
                    """
                    {
                        "id": "${message.id}",
                        "content": "${message.content.replace("\"", "\\\"")}",
                        "isFromUser": ${message.isFromUser},
                        "timestamp": "${dateFormat.format(Date(message.timestamp))}"
                    }
                    """.trimIndent()
                }}
            ]
        }
        """.trimIndent()
        
        return json
    }
    
    private fun formatMarkdownContent(content: String): String {
        // Simple code block formatting
        return content.replace("```", "```")
    }
}

enum class ExportFormat(val displayName: String, val fileExtension: String) {
    TEXT("Texto plano", "txt"),
    MARKDOWN("Markdown", "md"),
    JSON("JSON", "json")
}

sealed class ExportResult {
    data class Success(val content: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}
