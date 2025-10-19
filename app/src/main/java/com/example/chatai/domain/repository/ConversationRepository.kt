package com.example.chatai.domain.repository

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    // Conversaciones
    fun getAllConversations(): Flow<List<Conversation>>
    fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>>
    fun getConversationById(id: String): Flow<Conversation?>
    suspend fun createConversation(title: String, model: String): Conversation
    suspend fun updateConversation(conversation: Conversation)
    suspend fun deleteConversation(id: String)
    suspend fun archiveConversation(id: String)
    suspend fun unarchiveConversation(id: String)
    
    // Mensajes
    fun getMessagesByConversationId(conversationId: String): Flow<List<Message>>
    suspend fun addMessage(message: Message)
    suspend fun deleteMessage(id: String)
    
    // Búsqueda
    suspend fun searchConversations(query: String): Flow<List<Conversation>>
}
