package com.example.chatai.data.local.dao

import androidx.room.*
import com.example.chatai.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    // Issue #126: Order favorites first, then by date
    @Query("SELECT * FROM conversations ORDER BY isFavorite DESC, lastActivity DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>
    
    @Query("SELECT * FROM conversations WHERE isArchived = :isArchived ORDER BY isFavorite DESC, lastActivity DESC")
    fun getConversationsByArchivedStatus(isArchived: Boolean): Flow<List<ConversationEntity>>
    
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationById(id: String): Flow<ConversationEntity?>
    
    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationByIdOnce(id: String): ConversationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)
    
    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
    
    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)
    
    @Query("UPDATE conversations SET isArchived = 1 WHERE id = :id")
    suspend fun archiveConversation(id: String)
    
    @Query("UPDATE conversations SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveConversation(id: String)
    
    // Issue #123-125: Toggle favorite status
    @Query("UPDATE conversations SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)
    
    // Issue #124: Get only favorite conversations
    @Query("SELECT * FROM conversations WHERE isFavorite = 1 AND isArchived = 0 ORDER BY lastActivity DESC")
    fun getFavoriteConversations(): Flow<List<ConversationEntity>>
    
    // Issue #126: Search also orders favorites first
    @Query("SELECT * FROM conversations WHERE (title LIKE '%' || :query || '%' OR lastMessage LIKE '%' || :query || '%') ORDER BY isFavorite DESC, lastActivity DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>
}
