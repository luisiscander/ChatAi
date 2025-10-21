package com.example.chatai.domain.model

import java.util.Date

data class Conversation(
    val id: String,
    val title: String,
    val model: String,
    val lastMessage: String?,
    val lastActivity: Date,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false,
    val isPrivate: Boolean = false, // Issue #138: Private conversations with PIN
    val createdAt: Date,
    val updatedAt: Date
)

data class Message(
    val id: String,
    val conversationId: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date,
    val model: String?,
    val inputTokens: Int? = null,
    val outputTokens: Int? = null,
    val totalTokens: Int? = null,
    val estimatedCost: Double? = null
)

enum class ModelType(val displayName: String, val icon: String) {
    GPT4("GPT-4", "gpt4"),
    CLAUDE3_OPUS("Claude 3 Opus", "claude3"),
    CLAUDE3_SONNET("Claude 3 Sonnet", "claude3"),
    CLAUDE3_HAIKU("Claude 3 Haiku", "claude3"),
    LLAMA3_70B("Llama 3 70B", "llama3"),
    LLAMA3_8B("Llama 3 8B", "llama3"),
    GEMINI_PRO("Gemini Pro", "gemini"),
    UNKNOWN("Unknown", "unknown");
    
    companion object {
        fun fromModelId(modelId: String): ModelType {
            return when {
                modelId.contains("gpt-4") -> GPT4
                modelId.contains("claude-3-opus") -> CLAUDE3_OPUS
                modelId.contains("claude-3-sonnet") -> CLAUDE3_SONNET
                modelId.contains("claude-3-haiku") -> CLAUDE3_HAIKU
                modelId.contains("llama-3-70b") -> LLAMA3_70B
                modelId.contains("llama-3-8b") -> LLAMA3_8B
                modelId.contains("gemini-pro") -> GEMINI_PRO
                else -> UNKNOWN
            }
        }
    }
}
