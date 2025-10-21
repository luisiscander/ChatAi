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
    val model: String?,
    val inputTokens: Int? = null,
    val outputTokens: Int? = null,
    val totalTokens: Int? = null,
    val estimatedCost: Double? = null
) {
    fun toDomain(): com.example.chatai.domain.model.Message {
        return com.example.chatai.domain.model.Message(
            id = id,
            conversationId = conversationId,
            content = content,
            isFromUser = isFromUser,
            timestamp = Date(timestamp),
            model = model,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            totalTokens = totalTokens,
            estimatedCost = estimatedCost
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
                model = message.model,
                inputTokens = message.inputTokens,
                outputTokens = message.outputTokens,
                totalTokens = message.totalTokens,
                estimatedCost = message.estimatedCost
            )
        }
    }
}
