package com.example.chatai.data.remote.api

import com.example.chatai.data.remote.dto.ChatCompletionRequest
import com.example.chatai.data.remote.dto.ChatCompletionResponse
import com.example.chatai.data.remote.dto.OpenRouterModelsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenRouterApiService {

    /**
     * Validates API key by fetching available models
     * This endpoint requires authentication and will fail with invalid API key
     */
    @GET("models")
    suspend fun getModels(
        @Header("Authorization") authorization: String
    ): Response<OpenRouterModelsResponse>
    
    /**
     * Chat completion with streaming support
     * https://openrouter.ai/docs#models
     */
    @POST("chat/completions")
    @Streaming
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://github.com/luisiscander/ChatAi",
        @Header("X-Title") title: String = "ChatAi Android",
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>
}
