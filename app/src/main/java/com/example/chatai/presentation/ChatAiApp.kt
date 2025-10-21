package com.example.chatai.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chatai.ChatAiViewModel
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.navigation.NavigationManager
import com.example.chatai.presentation.navigation.Routes

@Composable
fun ChatAiApp(
    modifier: Modifier = Modifier,
    viewModel: ChatAiViewModel = hiltViewModel()
) {
    val onboardingStatus by viewModel.onboardingStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.checkOnboardingStatus()
    }

    LaunchedEffect(onboardingStatus, isLoading) {
        if (!isLoading) {
            when (onboardingStatus) {
                OnboardingStatus.ShowOnboarding -> navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } }
                OnboardingStatus.ShowApiKeySetup -> navController.navigate(Routes.API_KEY_SETUP) { popUpTo(Routes.SPLASH) { inclusive = true } }
                OnboardingStatus.ShowMainApp -> navController.navigate(Routes.CONVERSATION_LIST) { popUpTo(Routes.SPLASH) { inclusive = true } }
                null -> {}
            }
        }
    }

    NavigationManager(
        navController = navController,
        onContinueClicked = {
            viewModel.completeOnboarding()
        },
        onApiKeyConfigured = {
            viewModel.completeApiKeySetup()
        },
        modifier = modifier
    )
}
