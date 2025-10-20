package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): ThemeMode {
        return userPreferencesRepository.getThemeMode()
    }
}
