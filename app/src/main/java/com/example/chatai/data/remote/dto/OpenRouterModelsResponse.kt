package com.example.chatai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenRouterModelsResponse(
    @SerializedName("data")
    val models: List<AiModel>
)

data class AiModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("context_length")
    val contextLength: Int? = null,
    
    @SerializedName("pricing")
    val pricing: Pricing? = null
)

data class Pricing(
    @SerializedName("prompt")
    val prompt: String? = null,
    
    @SerializedName("completion")
    val completion: String? = null
)
