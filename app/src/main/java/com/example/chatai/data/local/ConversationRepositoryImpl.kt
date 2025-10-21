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
        return conversationDao.getAllConversations()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>> {
        return conversationDao.getConversationsByArchivedStatus(isArchived)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getConversationById(id: String): Flow<Conversation?> {
        return conversationDao.getConversationById(id)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            model = model,
            lastMessage = null,
            lastActivity = Date(),
            isArchived = false,
            isFavorite = false,
            isPrivate = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        // Guardar en la base de datos
        conversationDao.insertConversation(ConversationEntity.fromDomain(conversation))
        
        return conversation
    }

    override suspend fun updateConversation(conversation: Conversation) {
        conversationDao.updateConversation(ConversationEntity.fromDomain(conversation))
    }

    override suspend fun deleteConversation(id: String) {
        conversationDao.deleteConversation(id)
        // También eliminar todos los mensajes de la conversación
        messageDao.deleteMessagesByConversationId(id)
    }

    override suspend fun archiveConversation(id: String) {
        conversationDao.archiveConversation(id)
    }

    override suspend fun unarchiveConversation(id: String) {
        conversationDao.unarchiveConversation(id)
    }
    
    override suspend fun toggleFavorite(id: String) {
        conversationDao.toggleFavorite(id)
    }
    
    override fun getFavoriteConversations(): Flow<List<Conversation>> {
        return conversationDao.getFavoriteConversations()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMessagesByConversationId(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesByConversationId(conversationId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addMessage(message: Message) {
        // Guardar el mensaje
        messageDao.insertMessage(MessageEntity.fromDomain(message))
        
        // Actualizar la conversación con el último mensaje
        val conversation = conversationDao.getConversationByIdOnce(message.conversationId)
        conversation?.let {
            val updatedConversation = it.copy(
                lastMessage = message.content,
                lastActivity = message.timestamp.time,
                updatedAt = System.currentTimeMillis()
            )
            conversationDao.updateConversation(updatedConversation)
        }
    }

    override suspend fun deleteMessage(id: String) {
        messageDao.deleteMessage(id)
    }

    override suspend fun searchConversations(query: String): Flow<List<Conversation>> {
        return conversationDao.searchConversations(query)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    // Issue #131: Get recent messages with pagination
    override suspend fun getRecentMessages(conversationId: String, limit: Int): List<Message> {
        return messageDao.getRecentMessages(conversationId, limit)
            .map { it.toDomain() }
    }
    
    // Issue #132: Load older messages for infinite scroll
    override suspend fun getMessagesBeforeTimestamp(conversationId: String, beforeTimestamp: Long, limit: Int): List<Message> {
        return messageDao.getMessagesBeforeTimestamp(conversationId, beforeTimestamp, limit)
            .map { it.toDomain() }
    }
    
    // Issue #133: Search in all messages
    override suspend fun searchMessagesInConversation(conversationId: String, query: String): List<Message> {
        return messageDao.searchMessagesInConversation(conversationId, query)
            .map { it.toDomain() }
    }
    
    override suspend fun getMessageCount(conversationId: String): Int {
        return messageDao.getMessageCount(conversationId)
    }
}