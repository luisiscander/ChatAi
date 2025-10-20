package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        userPreferencesRepository.setThemeMode(themeMode)
    }
}
