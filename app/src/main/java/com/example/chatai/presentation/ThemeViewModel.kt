package com.example.chatai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.domain.usecase.GetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getThemeModeUseCase: GetThemeModeUseCase
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        loadThemeMode()
    }

    private fun loadThemeMode() {
        viewModelScope.launch {
            try {
                _themeMode.value = getThemeModeUseCase()
            } catch (e: Exception) {
                // Fallback to system theme if there's an error
                _themeMode.value = ThemeMode.SYSTEM
            }
        }
    }

    fun updateThemeMode(newThemeMode: ThemeMode) {
        _themeMode.value = newThemeMode
    }
}
