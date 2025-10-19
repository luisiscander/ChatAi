package com.example.chatai.data.local

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    // TODO: Add Room dependencies when compilation issues are resolved
) : ConversationRepository {

    // Temporary implementation - returns empty data
    override fun getAllConversations(): Flow<List<Conversation>> {
        return flowOf(emptyList())
    }

    override fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>> {
        return flowOf(emptyList())
    }

    override fun getConversationById(id: String): Flow<Conversation?> {
        return flowOf(null)
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        return Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            model = model,
            lastMessage = null,
            lastActivity = Date(),
            isArchived = false,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    override suspend fun updateConversation(conversation: Conversation) {
        // TODO: Implement when Room is ready
    }

    override suspend fun deleteConversation(id: String) {
        // TODO: Implement when Room is ready
    }

    override suspend fun archiveConversation(id: String) {
        // TODO: Implement when Room is ready
    }

    override suspend fun unarchiveConversation(id: String) {
        // TODO: Implement when Room is ready
    }

    override fun getMessagesByConversationId(conversationId: String): Flow<List<Message>> {
        return flowOf(emptyList())
    }

    override suspend fun addMessage(message: Message) {
        // TODO: Implement when Room is ready
    }

    override suspend fun deleteMessage(id: String) {
        // TODO: Implement when Room is ready
    }

    override suspend fun searchConversations(query: String): Flow<List<Conversation>> {
        return flowOf(emptyList())
    }
}