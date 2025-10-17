package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class CheckOnboardingStatusUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): OnboardingStatus {
        val isFirstTime = userPreferencesRepository.isFirstTimeUser()
        val hasApiKey = userPreferencesRepository.hasApiKey()
        
        return when {
            isFirstTime -> OnboardingStatus.ShowOnboarding
            !hasApiKey -> OnboardingStatus.ShowApiKeySetup
            else -> OnboardingStatus.ShowMainApp
        }
    }
}

sealed class OnboardingStatus {
    object ShowOnboarding : OnboardingStatus()
    object ShowApiKeySetup : OnboardingStatus()
    object ShowMainApp : OnboardingStatus()
}
