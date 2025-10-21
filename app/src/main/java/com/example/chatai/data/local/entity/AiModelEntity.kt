package com.example.chatai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ModelPricing
import com.example.chatai.domain.model.RateLimits

@Entity(tableName = "ai_models")
data class AiModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val company: String,
    val context: String,
    val inputPrice: String,
    val outputPrice: String,
    val inputPricePer1k: Double,
    val outputPricePer1k: Double,
    val description: String?,
    val capabilities: String, // JSON string of list
    val requestsPerMinute: Int?,
    val tokensPerMinute: Int?,
    val documentationUrl: String?,
    val lastUpdated: Long // Timestamp for cache invalidation
) {
    fun toDomain(): AiModel {
        return AiModel(
            id = id,
            name = name,
            company = company,
            context = context,
            pricing = ModelPricing(
                input = inputPrice,
                output = outputPrice,
                inputPricePer1k = inputPricePer1k,
                outputPricePer1k = outputPricePer1k
            ),
            description = description,
            capabilities = parseCapabilities(capabilities),
            rateLimits = if (requestsPerMinute != null || tokensPerMinute != null) {
                RateLimits(
                    requestsPerMinute = requestsPerMinute,
                    tokensPerMinute = tokensPerMinute
                )
            } else null,
            documentationUrl = documentationUrl
        )
    }

    companion object {
        fun fromDomain(model: AiModel): AiModelEntity {
            return AiModelEntity(
                id = model.id,
                name = model.name,
                company = model.company,
                context = model.context,
                inputPrice = model.pricing.input,
                outputPrice = model.pricing.output,
                inputPricePer1k = model.pricing.inputPricePer1k,
                outputPricePer1k = model.pricing.outputPricePer1k,
                description = model.description,
                capabilities = serializeCapabilities(model.capabilities),
                requestsPerMinute = model.rateLimits?.requestsPerMinute,
                tokensPerMinute = model.rateLimits?.tokensPerMinute,
                documentationUrl = model.documentationUrl,
                lastUpdated = System.currentTimeMillis()
            )
        }

        private fun serializeCapabilities(capabilities: List<String>): String {
            return capabilities.joinToString(separator = "|")
        }

        private fun parseCapabilities(capabilities: String): List<String> {
            return if (capabilities.isBlank()) emptyList()
            else capabilities.split("|")
        }
    }

    private fun parseCapabilities(capabilities: String): List<String> {
        return if (capabilities.isBlank()) emptyList()
        else capabilities.split("|")
    }
}

