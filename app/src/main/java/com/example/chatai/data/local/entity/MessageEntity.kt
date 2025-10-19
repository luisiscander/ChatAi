package com.example.chatai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long, // Timestamp
    val model: String?
) {
    fun toDomain(): com.example.chatai.domain.model.Message {
        return com.example.chatai.domain.model.Message(
            id = id,
            conversationId = conversationId,
            content = content,
            isFromUser = isFromUser,
            timestamp = Date(timestamp),
            model = model
        )
    }
    
    companion object {
        fun fromDomain(message: com.example.chatai.domain.model.Message): MessageEntity {
            return MessageEntity(
                id = message.id,
                conversationId = message.conversationId,
                content = message.content,
                isFromUser = message.isFromUser,
                timestamp = message.timestamp.time,
                model = message.model
            )
        }
    }
}
