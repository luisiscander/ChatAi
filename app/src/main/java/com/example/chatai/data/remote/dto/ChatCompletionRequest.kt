package com.example.chatai.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for OpenRouter chat completion API
 * https://openrouter.ai/docs#models
 */
data class ChatCompletionRequest(
    @SerializedName("model")
    val model: String,
    
    @SerializedName("messages")
    val messages: List<ChatMessage>,
    
    @SerializedName("stream")
    val stream: Boolean = true,
    
    @SerializedName("temperature")
    val temperature: Double? = null,
    
    @SerializedName("max_tokens")
    val maxTokens: Int? = null,
    
    @SerializedName("top_p")
    val topP: Double? = null
)

data class ChatMessage(
    @SerializedName("role")
    val role: String, // "user" or "assistant" or "system"
    
    @SerializedName("content")
    val content: String
)

