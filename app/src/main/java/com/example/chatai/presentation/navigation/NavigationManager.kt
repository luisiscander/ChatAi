package com.example.chatai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.MainScreen
import com.example.chatai.presentation.screens.OnboardingScreen

@Composable
fun NavigationManager(
    onboardingStatus: OnboardingStatus?,
    onContinueClicked: () -> Unit,
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (onboardingStatus) {
        OnboardingStatus.ShowOnboarding -> {
            OnboardingScreen(
                onContinueClicked = onContinueClicked,
                modifier = modifier
            )
        }
        OnboardingStatus.ShowApiKeySetup -> {
            ApiKeySetupScreen(
                onApiKeyConfigured = onApiKeyConfigured,
                modifier = modifier
            )
        }
        OnboardingStatus.ShowMainApp -> {
            MainScreen(
                modifier = modifier
            )
        }
        null -> {
            // Mostrar pantalla de carga mientras se verifica el estado
            MainScreen(modifier = modifier)
        }
    }
}
