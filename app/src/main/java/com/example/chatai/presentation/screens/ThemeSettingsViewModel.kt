package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.domain.usecase.GetThemeModeUseCase
import com.example.chatai.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeSettingsUiState())
    val uiState: StateFlow<ThemeSettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentTheme()
    }

    private fun loadCurrentTheme() {
        viewModelScope.launch {
            val currentTheme = getThemeModeUseCase()
            _uiState.value = _uiState.value.copy(
                selectedThemeMode = currentTheme,
                originalThemeMode = currentTheme
            )
        }
    }

    fun selectTheme(themeMode: ThemeMode) {
        _uiState.value = _uiState.value.copy(
            selectedThemeMode = themeMode,
            showPreview = true,
            hasChanges = _uiState.value.originalThemeMode != themeMode
        )
    }

    fun applyTheme() {
        viewModelScope.launch {
            setThemeModeUseCase(_uiState.value.selectedThemeMode)
            _uiState.value = _uiState.value.copy(
                originalThemeMode = _uiState.value.selectedThemeMode,
                showPreview = false,
                hasChanges = false
            )
        }
    }

    fun cancelPreview() {
        _uiState.value = _uiState.value.copy(
            selectedThemeMode = _uiState.value.originalThemeMode,
            showPreview = false,
            hasChanges = false
        )
    }
}

data class ThemeSettingsUiState(
    val selectedThemeMode: ThemeMode = ThemeMode.SYSTEM,
    val originalThemeMode: ThemeMode = ThemeMode.SYSTEM,
    val showPreview: Boolean = false,
    val hasChanges: Boolean = false
)
