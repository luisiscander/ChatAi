package com.example.chatai.domain.usecase

import android.content.Context
import android.content.Intent
import com.example.chatai.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Use Case para compartir una conversación completa
 */
class ShareConversationUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        conversationTitle: String,
        messages: List<Message>
    ): ShareResult {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            val shareText = buildString {
                appendLine("=== $conversationTitle ===")
                appendLine()
                
                messages.forEach { message ->
                    val sender = if (message.isFromUser) "Tú" else (message.model ?: "Asistente")
                    appendLine("[$sender - ${dateFormat.format(message.timestamp)}]")
                    appendLine(message.content)
                    appendLine()
                }
                
                appendLine("---")
                appendLine("Exportado desde Chat AI")
            }
            
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            
            val shareIntent = Intent.createChooser(sendIntent, "Compartir conversación")
            context.startActivity(shareIntent)
            
            ShareResult.Success
        } catch (e: Exception) {
            ShareResult.Error("Error al compartir conversación: ${e.message}")
        }
    }
}

