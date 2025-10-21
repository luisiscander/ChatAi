package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use Case para verificar si el usuario ha alcanzado el límite de uso
 */
class CheckUsageLimitUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getTotalUsageStatisticsUseCase: GetTotalUsageStatisticsUseCase
) {
    suspend operator fun invoke(): UsageLimitResult {
        return try {
            // Get monthly limit from preferences (default $10)
            val monthlyLimit = userPreferencesRepository.getMonthlyUsageLimit() ?: 10.0
            
            // Get current usage
            when (val statsResult = getTotalUsageStatisticsUseCase()) {
                is TotalUsageStatisticsResult.Success -> {
                    val currentUsage = statsResult.statistics.totalCost
                    val usagePercentage = (currentUsage / monthlyLimit) * 100
                    
                    when {
                        usagePercentage >= 100 -> {
                            UsageLimitResult.LimitExceeded(
                                currentUsage = currentUsage,
                                limit = monthlyLimit,
                                percentage = usagePercentage
                            )
                        }
                        usagePercentage >= 80 -> {
                            UsageLimitResult.HighUsage(
                                currentUsage = currentUsage,
                                limit = monthlyLimit,
                                percentage = usagePercentage
                            )
                        }
                        else -> {
                            UsageLimitResult.Normal(
                                currentUsage = currentUsage,
                                limit = monthlyLimit,
                                percentage = usagePercentage
                            )
                        }
                    }
                }
                is TotalUsageStatisticsResult.Error -> {
                    UsageLimitResult.Error(statsResult.message)
                }
            }
        } catch (e: Exception) {
            UsageLimitResult.Error("Error al verificar límite de uso: ${e.message}")
        }
    }
}

sealed class UsageLimitResult {
    data class Normal(
        val currentUsage: Double,
        val limit: Double,
        val percentage: Double
    ) : UsageLimitResult()
    
    data class HighUsage(
        val currentUsage: Double,
        val limit: Double,
        val percentage: Double
    ) : UsageLimitResult()
    
    data class LimitExceeded(
        val currentUsage: Double,
        val limit: Double,
        val percentage: Double
    ) : UsageLimitResult()
    
    data class Error(val message: String) : UsageLimitResult()
}

