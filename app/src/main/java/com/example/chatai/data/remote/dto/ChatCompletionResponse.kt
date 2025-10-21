package com.example.chatai.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for OpenRouter chat completion API (streaming)
 * https://openrouter.ai/docs#models
 */
data class ChatCompletionResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("model")
    val model: String,
    
    @SerializedName("choices")
    val choices: List<Choice>,
    
    @SerializedName("usage")
    val usage: Usage? = null
)

data class Choice(
    @SerializedName("index")
    val index: Int,
    
    @SerializedName("delta")
    val delta: Delta? = null,
    
    @SerializedName("message")
    val message: MessageContent? = null,
    
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Delta(
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("content")
    val content: String? = null
)

data class MessageContent(
    @SerializedName("role")
    val role: String,
    
    @SerializedName("content")
    val content: String
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    
    @SerializedName("total_tokens")
    val totalTokens: Int
)

