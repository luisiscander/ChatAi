package com.example.chatai.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.ChatAiViewModel
import com.example.chatai.presentation.navigation.NavigationManager

@Composable
fun ChatAiApp(
    modifier: Modifier = Modifier,
    viewModel: ChatAiViewModel = hiltViewModel()
) {
    val onboardingStatus by viewModel.onboardingStatus.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.checkOnboardingStatus()
    }
    
    NavigationManager(
        onboardingStatus = onboardingStatus,
        onContinueClicked = {
            viewModel.completeOnboarding()
        },
        onApiKeyConfigured = {
            viewModel.completeApiKeySetup()
        },
        modifier = modifier
    )
}
