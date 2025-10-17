package com.example.chatai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.MainScreen
import com.example.chatai.presentation.screens.OnboardingScreen
import com.example.chatai.ui.theme.ChatAiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatAiApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatAiApp(
    modifier: Modifier = Modifier,
    viewModel: ChatAiViewModel = hiltViewModel()
) {
    val onboardingStatus by viewModel.onboardingStatus.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.checkOnboardingStatus()
    }
    
    when (onboardingStatus) {
        OnboardingStatus.ShowOnboarding -> {
            OnboardingScreen(
                onContinueClicked = {
                    viewModel.completeOnboarding()
                },
                modifier = modifier
            )
        }
        OnboardingStatus.ShowApiKeySetup -> {
            ApiKeySetupScreen(
                onApiKeyConfigured = {
                    viewModel.completeApiKeySetup()
                },
                modifier = modifier
            )
        }
        OnboardingStatus.ShowMainApp -> {
            MainScreen(
                modifier = modifier
            )
        }
    }
}