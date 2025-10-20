package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetDefaultModelUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): String? {
        return userPreferencesRepository.getDefaultModel()
    }
}
