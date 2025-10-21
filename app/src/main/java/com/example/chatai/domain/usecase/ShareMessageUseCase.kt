package com.example.chatai.domain.usecase

import android.content.Context
import android.content.Intent
import javax.inject.Inject

/**
 * Use Case para compartir un mensaje individual
 */
class ShareMessageUseCase @Inject constructor() {
    operator fun invoke(context: Context, messageContent: String, model: String?): ShareResult {
        return try {
            val shareText = buildString {
                appendLine(messageContent)
                appendLine()
                model?.let {
                    appendLine("---")
                    appendLine("Generado por: $it")
                }
            }
            
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            
            val shareIntent = Intent.createChooser(sendIntent, "Compartir mensaje")
            context.startActivity(shareIntent)
            
            ShareResult.Success
        } catch (e: Exception) {
            ShareResult.Error("Error al compartir mensaje: ${e.message}")
        }
    }
}

sealed class ShareResult {
    object Success : ShareResult()
    data class Error(val message: String) : ShareResult()
}

