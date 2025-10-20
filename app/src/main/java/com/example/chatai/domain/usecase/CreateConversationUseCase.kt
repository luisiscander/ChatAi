package com.example.chatai.domain.usecase

import android.content.Context
import com.example.chatai.R
import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): CreateConversationResult {
        return try {
            // Get default model from preferences
            val defaultModel = userPreferencesRepository.getDefaultModel() ?: "gpt-3.5-turbo"
            
            // Create new conversation with default title
            val conversation = conversationRepository.createConversation(
                title = context.getString(R.string.new_conversation_title),
                model = defaultModel
            )
            
            CreateConversationResult.Success(conversation)
        } catch (e: Exception) {
            CreateConversationResult.Error(e.message ?: "Error desconocido al crear conversación")
        }
    }
}

sealed class CreateConversationResult {
    data class Success(val conversation: Conversation) : CreateConversationResult()
    data class Error(val message: String) : CreateConversationResult()
}