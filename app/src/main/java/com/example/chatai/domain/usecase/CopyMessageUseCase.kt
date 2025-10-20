package com.example.chatai.domain.usecase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CopyMessageUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(messageText: String): CopyMessageResult {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Mensaje copiado", messageText)
            clipboard.setPrimaryClip(clip)
            CopyMessageResult.Success
        } catch (e: Exception) {
            CopyMessageResult.Error(e.message ?: "Error al copiar mensaje")
        }
    }
}

sealed class CopyMessageResult {
    object Success : CopyMessageResult()
    data class Error(val message: String) : CopyMessageResult()
}
