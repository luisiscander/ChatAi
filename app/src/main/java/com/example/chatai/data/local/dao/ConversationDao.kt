package com.example.chatai.data.local.dao

import androidx.room.*
import com.example.chatai.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = :isArchived ORDER BY lastActivity DESC")
    fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<ConversationEntity>>
    
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationById(id: String): Flow<ConversationEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)
    
    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
    
    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)
    
    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)
    
    @Query("UPDATE conversations SET isArchived = 1 WHERE id = :id")
    suspend fun archiveConversation(id: String)
    
    @Query("UPDATE conversations SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveConversation(id: String)
    
    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' OR lastMessage LIKE '%' || :query || '%' ORDER BY lastActivity DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>
}
