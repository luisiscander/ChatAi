package com.example.chatai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.CheckOnboardingStatusUseCase
import com.example.chatai.domain.usecase.CompleteOnboardingUseCase
import com.example.chatai.domain.usecase.OnboardingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ChatAiViewModel @Inject constructor(
    private val checkOnboardingStatusUseCase: CheckOnboardingStatusUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _onboardingStatus = MutableStateFlow<OnboardingStatus?>(null)
    val onboardingStatus: StateFlow<OnboardingStatus?> = _onboardingStatus.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun checkOnboardingStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            // Mostrar splash screen por al menos 2 segundos
            delay(2000)
            val status = checkOnboardingStatusUseCase()
            println("DEBUG: Onboarding status determined: $status")
            _onboardingStatus.value = status
            _isLoading.value = false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase()
            checkOnboardingStatus()
        }
    }

    fun completeApiKeySetup() {
        // Por ahora, solo actualizamos el estado
        // En el futuro, aquí se guardará la API key
        viewModelScope.launch {
            checkOnboardingStatus()
        }
    }
}
