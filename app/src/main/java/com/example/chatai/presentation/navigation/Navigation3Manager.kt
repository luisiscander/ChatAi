package com.example.chatai.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.ChatScreen
import com.example.chatai.presentation.screens.ConversationListScreen
import com.example.chatai.presentation.screens.DefaultModelSettingsScreen
import com.example.chatai.presentation.screens.ExportConversationScreen
import com.example.chatai.presentation.screens.OnboardingScreen
import com.example.chatai.presentation.screens.SplashScreen
import com.example.chatai.presentation.screens.ThemeSettingsScreen
import com.example.chatai.presentation.screens.ConversationCreationViewModel
import com.example.chatai.domain.usecase.GetConversationByIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Navigation 3 - Simple back stack implementation
data class NavEntry(
    val key: String,
    val data: Any? = null
)

@Composable
fun Navigation3Manager(
    onboardingStatus: OnboardingStatus?,
    isLoading: Boolean,
    onContinueClicked: () -> Unit,
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier
) {
    var backStack by remember { mutableStateOf<List<NavEntry>>(emptyList()) }
    
    // Conversation creation ViewModel
    val conversationCreationViewModel = hiltViewModel<ConversationCreationViewModel>()
    val conversationCreationState by conversationCreationViewModel.uiState.collectAsState()
    
    // Handle conversation creation result
    LaunchedEffect(conversationCreationState.conversationId, conversationCreationState.error) {
        conversationCreationState.conversationId?.let { conversationId ->
            // Navigate to chat with the created conversation ID
            backStack = backStack + NavEntry("chat", conversationId)
            conversationCreationViewModel.clearState()
        }
        
        conversationCreationState.error?.let { error ->
            // Handle error - could show a snackbar or error dialog
            println("Error creating conversation: $error")
            conversationCreationViewModel.clearState()
        }
    }
    
    // Determine initial route
    LaunchedEffect(onboardingStatus, isLoading) {
        // If we're still loading, show splash
        if (isLoading) {
            if (backStack.isEmpty() || backStack.firstOrNull()?.key != "splash") {
                backStack = listOf(NavEntry("splash"))
            }
            return@LaunchedEffect
        }
        
        // Loading is complete, determine next screen based on onboarding status
        val targetEntry = when (onboardingStatus) {
            OnboardingStatus.ShowOnboarding -> NavEntry("onboarding")
            OnboardingStatus.ShowApiKeySetup -> NavEntry("api_key_setup")
            OnboardingStatus.ShowMainApp -> NavEntry("conversation_list")
            null -> NavEntry("splash")
        }
        
        // Update back stack if we need to navigate away from splash
        val currentFirst = backStack.firstOrNull()
        if (currentFirst?.key == "splash" && !isLoading) {
            backStack = listOf(targetEntry)
        } else if (backStack.isEmpty()) {
            backStack = listOf(targetEntry)
        }
    }
    
    // Display current destination
    val currentDestination = backStack.lastOrNull()
    
    when (currentDestination?.key) {
        "splash" -> {
            SplashScreen(modifier = modifier)
        }
        "onboarding" -> {
            OnboardingScreen(
                onContinueClicked = {
                    onContinueClicked()
                    backStack = backStack + NavEntry("api_key_setup")
                },
                modifier = modifier
            )
        }
        "api_key_setup" -> {
            ApiKeySetupScreen(
                onApiKeyConfigured = {
                    onApiKeyConfigured()
                    backStack = backStack + NavEntry("conversation_list")
                },
                modifier = modifier
            )
        }
        "conversation_list" -> {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    backStack = backStack + NavEntry("chat", conversationId)
                },
                onCreateConversation = {
                    conversationCreationViewModel.createConversation()
                },
                onShowArchived = {
                    backStack = backStack + NavEntry("archived_conversations")
                },
                onNavigateToThemeSettings = {
                    backStack = backStack + NavEntry("theme_settings")
                },
                onNavigateToDefaultModelSettings = {
                    backStack = backStack + NavEntry("default_model_settings")
                },
                modifier = modifier
            )
        }
        "chat" -> {
            val conversationId = currentDestination.data as? String ?: ""
            ChatScreen(
                conversationId = conversationId,
                onBackClicked = {
                    backStack = backStack.dropLast(1)
                },
                onNavigateToExportConversation = {
                    backStack = backStack + NavEntry("export_conversation", conversationId)
                },
                modifier = modifier
            )
        }
        "archived_conversations" -> {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    backStack = backStack + NavEntry("chat", conversationId)
                },
                onCreateConversation = {
                    conversationCreationViewModel.createConversation()
                },
                onShowArchived = {
                    backStack = backStack.dropLast(1)
                },
                onNavigateToThemeSettings = {
                    backStack = backStack + NavEntry("theme_settings")
                },
                onNavigateToDefaultModelSettings = {
                    backStack = backStack + NavEntry("default_model_settings")
                },
                modifier = modifier
            )
        }
        "theme_settings" -> {
            ThemeSettingsScreen(
                onBackClicked = {
                    backStack = backStack.dropLast(1)
                },
                modifier = modifier
            )
        }
        "default_model_settings" -> {
            DefaultModelSettingsScreen(
                onBackClicked = {
                    backStack = backStack.dropLast(1)
                },
                modifier = modifier
            )
        }
        "export_conversation" -> {
            val conversationId = currentDestination.data as? String ?: ""
            ExportConversationScreen(
                conversationId = conversationId,
                onBackClicked = {
                    backStack = backStack.dropLast(1)
                },
                modifier = modifier
            )
        }
    }
}
