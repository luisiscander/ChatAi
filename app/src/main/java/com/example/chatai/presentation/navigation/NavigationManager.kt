package com.example.chatai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.ConversationListScreen
import com.example.chatai.presentation.screens.MainScreen
import com.example.chatai.presentation.screens.OnboardingScreen
import com.example.chatai.presentation.screens.SplashScreen

@Composable
fun NavigationManager(
    onboardingStatus: OnboardingStatus?,
    isLoading: Boolean,
    onContinueClicked: () -> Unit,
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            SplashScreen(modifier = modifier)
        }
        onboardingStatus == OnboardingStatus.ShowOnboarding -> {
            OnboardingScreen(
                onContinueClicked = onContinueClicked,
                modifier = modifier
            )
        }
        onboardingStatus == OnboardingStatus.ShowApiKeySetup -> {
            ApiKeySetupScreen(
                onApiKeyConfigured = onApiKeyConfigured,
                modifier = modifier
            )
        }
        onboardingStatus == OnboardingStatus.ShowMainApp -> {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    // TODO: Navigate to chat screen
                },
                onCreateConversation = {
                    // TODO: Navigate to create conversation
                },
                onShowArchived = {
                    // TODO: Show archived conversations
                },
                modifier = modifier
            )
        }
        else -> {
            // Fallback a splash screen
            SplashScreen(modifier = modifier)
        }
    }
}
