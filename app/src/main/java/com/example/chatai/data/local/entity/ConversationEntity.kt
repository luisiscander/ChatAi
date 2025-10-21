package com.example.chatai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val model: String,
    val lastMessage: String?,
    val lastActivity: Long, // Timestamp
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long, // Timestamp
    val updatedAt: Long // Timestamp
) {
    fun toDomain(): com.example.chatai.domain.model.Conversation {
        return com.example.chatai.domain.model.Conversation(
            id = id,
            title = title,
            model = model,
            lastMessage = lastMessage,
            lastActivity = Date(lastActivity),
            isArchived = isArchived,
            isFavorite = isFavorite,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt)
        )
    }
    
    companion object {
        fun fromDomain(conversation: com.example.chatai.domain.model.Conversation): ConversationEntity {
            return ConversationEntity(
                id = conversation.id,
                title = conversation.title,
                model = conversation.model,
                lastMessage = conversation.lastMessage,
                lastActivity = conversation.lastActivity.time,
                isArchived = conversation.isArchived,
                isFavorite = conversation.isFavorite,
                createdAt = conversation.createdAt.time,
                updatedAt = conversation.updatedAt.time
            )
        }
    }
}
