package com.example.chatai.data.local.dao

import androidx.room.*
import com.example.chatai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    // Original query - gets all messages (use carefully)
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversationId(conversationId: String): Flow<List<MessageEntity>>
    
    // Issue #131: Get last N messages (paginated)
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(conversationId: String, limit: Int): List<MessageEntity>
    
    // Issue #132: Load older messages (pagination with offset)
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND timestamp < :beforeTimestamp ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getMessagesBeforeTimestamp(conversationId: String, beforeTimestamp: Long, limit: Int): List<MessageEntity>
    
    // Issue #133: Search all messages in a conversation
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchMessagesInConversation(conversationId: String, query: String): List<MessageEntity>
    
    // Get total message count for a conversation
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCount(conversationId: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversationId(conversationId: String)
}
