package com.example.chatai.data.local

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    // TODO: Add Room dependencies when compilation issues are resolved
) : ConversationRepository {

    // Temporary in-memory storage for conversations
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Temporary in-memory storage for messages
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    private val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversations
    }

    override fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<Conversation>> {
        return conversations
    }

    override fun getConversationById(id: String): Flow<Conversation?> {
        // Return a flow that emits the current value immediately
        android.util.Log.d("ConversationRepo", "Getting conversation by ID: $id")
        val currentConversation = _conversations.value.find { it.id == id }
        android.util.Log.d("ConversationRepo", "Found conversation: ${currentConversation != null}, Total in list: ${_conversations.value.size}")
        if (currentConversation != null) {
            android.util.Log.d("ConversationRepo", "Conversation title: ${currentConversation.title}")
        }
        return flowOf(currentConversation)
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        android.util.Log.d("ConversationRepo", "Creating conversation: title=$title, model=$model")
        val newConversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            model = model,
            lastMessage = null,
            lastActivity = Date(),
            isArchived = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        android.util.Log.d("ConversationRepo", "Created conversation with ID: ${newConversation.id}")
        
        // Add to in-memory storage
        val currentConversations = _conversations.value.toMutableList()
        currentConversations.add(newConversation)
        _conversations.value = currentConversations
        
        android.util.Log.d("ConversationRepo", "Conversation added to list. Total conversations: ${_conversations.value.size}")
        
        return newConversation
    }

    override suspend fun updateConversation(conversation: Conversation) {
        val currentConversations = _conversations.value.toMutableList()
        val index = currentConversations.indexOfFirst { it.id == conversation.id }
        if (index != -1) {
            currentConversations[index] = conversation
            _conversations.value = currentConversations
        }
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
        // Return a flow that emits the current value immediately
        val currentMessages = _messages.value[conversationId] ?: emptyList()
        return flowOf(currentMessages)
    }
    
    // Synchronous method to get messages by conversation ID immediately
    fun getMessagesByConversationIdSync(conversationId: String): List<Message> {
        return _messages.value[conversationId] ?: emptyList()
    }

    override suspend fun addMessage(message: Message) {
        val currentMessages = _messages.value.toMutableMap()
        val conversationMessages = currentMessages[message.conversationId]?.toMutableList() ?: mutableListOf()
        conversationMessages.add(message)
        currentMessages[message.conversationId] = conversationMessages
        _messages.value = currentMessages
    }

    override suspend fun deleteMessage(id: String) {
        // TODO: Implement when Room is ready
    }

    override suspend fun searchConversations(query: String): Flow<List<Conversation>> {
        return flowOf(emptyList())
    }
}