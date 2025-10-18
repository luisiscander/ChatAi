package com.example.chatai.data.local

import com.example.chatai.data.local.dao.ConversationDao
import com.example.chatai.data.local.dao.MessageDao
import com.example.chatai.data.local.entity.ConversationEntity
import com.example.chatai.data.local.entity.MessageEntity
import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationRepository {

    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getConversationsByArchivedStatus(false).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>> {
        return conversationDao.getConversationsByArchivedStatus(isArchived).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getConversationById(id: String): Flow<Conversation?> {
        return conversationDao.getConversationById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        
        val conversation = Conversation(
            id = id,
            title = title,
            model = model,
            lastMessage = null,
            lastActivity = Date(now),
            isArchived = false,
            createdAt = Date(now),
            updatedAt = Date(now)
        )
        
        conversationDao.insertConversation(ConversationEntity.fromDomain(conversation))
        return conversation
    }

    override suspend fun updateConversation(conversation: Conversation) {
        val updatedConversation = conversation.copy(updatedAt = Date())
        conversationDao.updateConversation(ConversationEntity.fromDomain(updatedConversation))
    }

    override suspend fun deleteConversation(id: String) {
        conversationDao.deleteConversationById(id)
        // También eliminar todos los mensajes de la conversación
        // messageDao.deleteMessagesByConversationId(id)
    }

    override suspend fun archiveConversation(id: String) {
        conversationDao.archiveConversation(id)
    }

    override suspend fun unarchiveConversation(id: String) {
        conversationDao.unarchiveConversation(id)
    }

    override fun getMessagesByConversationId(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesByConversationId(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addMessage(message: Message) {
        messageDao.insertMessage(MessageEntity.fromDomain(message))
    }

    override suspend fun deleteMessage(id: String) {
        messageDao.deleteMessageById(id)
    }

    override suspend fun searchConversations(query: String): Flow<List<Conversation>> {
        return conversationDao.searchConversations(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
