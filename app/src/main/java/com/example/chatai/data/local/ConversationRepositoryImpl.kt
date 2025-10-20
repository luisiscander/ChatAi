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

    // Temporary in-memory storage until Room is implemented
    private val conversations = mutableMapOf<String, Conversation>()
    private val messages = mutableMapOf<String, MutableList<Message>>()

    // Temporary implementation - returns stored data
    override fun getAllConversations(): Flow<List<Conversation>> {
        return flowOf(conversations.values.toList())
    }

    override fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>> {
        return flowOf(conversations.values.filter { it.isArchived == isArchived })
    }

    override fun getConversationById(id: String): Flow<Conversation?> {
        return flowOf(conversations[id])
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            model = model,
            lastMessage = null,
            lastActivity = Date(),
            isArchived = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        // Store the conversation in memory
        conversations[conversation.id] = conversation
        messages[conversation.id] = mutableListOf()
        return conversation
    }

    override suspend fun updateConversation(conversation: Conversation) {
        conversations[conversation.id] = conversation
    }

    override suspend fun deleteConversation(id: String) {
        conversations.remove(id)
        messages.remove(id)
    }

    override suspend fun archiveConversation(id: String) {
        conversations[id]?.let { conversation ->
            conversations[id] = conversation.copy(isArchived = true)
        }
    }

    override suspend fun unarchiveConversation(id: String) {
        conversations[id]?.let { conversation ->
            conversations[id] = conversation.copy(isArchived = false)
        }
    }

    override fun getMessagesByConversationId(conversationId: String): Flow<List<Message>> {
        return flowOf(messages[conversationId]?.toList() ?: emptyList())
    }

    override suspend fun addMessage(message: Message) {
        messages.getOrPut(message.conversationId) { mutableListOf() }.add(message)
        // Update conversation's last message and activity
        conversations[message.conversationId]?.let { conversation ->
            conversations[message.conversationId] = conversation.copy(
                lastMessage = message.content,
                lastActivity = Date(),
                updatedAt = Date()
            )
        }
    }

    override suspend fun deleteMessage(id: String) {
        // Find and remove message from all conversation lists
        messages.values.forEach { messageList ->
            messageList.removeAll { it.id == id }
        }
    }

    override suspend fun searchConversations(query: String): Flow<List<Conversation>> {
        val lowercaseQuery = query.lowercase()
        return flowOf(
            conversations.values.filter { conversation ->
                conversation.title.lowercase().contains(lowercaseQuery) ||
                conversation.lastMessage?.lowercase()?.contains(lowercaseQuery) == true
            }
        )
    }
}