package com.example.chatai.data.remote.api

import com.example.chatai.data.remote.dto.OpenRouterModelsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenRouterApiService {

    /**
     * Validates API key by fetching available models
     * This endpoint requires authentication and will fail with invalid API key
     */
    @GET("models")
    suspend fun getModels(
        @Header("Authorization") authorization: String
    ): Response<OpenRouterModelsResponse>
}
