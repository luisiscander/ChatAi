package com.example.chatai.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatai.domain.usecase.OnboardingStatus
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.ChatScreen
import com.example.chatai.presentation.screens.ConversationListScreen
import com.example.chatai.presentation.screens.MainScreen
import com.example.chatai.presentation.screens.OnboardingScreen
import com.example.chatai.presentation.screens.SplashScreen

// Navigation routes
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val API_KEY_SETUP = "api_key_setup"
    const val CONVERSATION_LIST = "conversation_list"
    const val CHAT = "chat/{conversationId}"
    const val ARCHIVED_CONVERSATIONS = "archived_conversations"
}

@Composable
fun NavigationManager(
    onboardingStatus: OnboardingStatus?,
    isLoading: Boolean,
    onContinueClicked: () -> Unit,
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    // Determine initial route based on onboarding status
    val startRoute = when {
        isLoading -> Routes.SPLASH
        onboardingStatus == OnboardingStatus.ShowOnboarding -> Routes.ONBOARDING
        onboardingStatus == OnboardingStatus.ShowApiKeySetup -> Routes.API_KEY_SETUP
        onboardingStatus == OnboardingStatus.ShowMainApp -> Routes.CONVERSATION_LIST
        else -> Routes.SPLASH
    }
    
    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(modifier = modifier)
        }
        
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onContinueClicked = {
                    onContinueClicked()
                    navController.navigate(Routes.API_KEY_SETUP)
                },
                modifier = modifier
            )
        }
        
        composable(Routes.API_KEY_SETUP) {
            ApiKeySetupScreen(
                onApiKeyConfigured = {
                    onApiKeyConfigured()
                    navController.navigate(Routes.CONVERSATION_LIST)
                },
                modifier = modifier
            )
        }
        
        composable(Routes.CONVERSATION_LIST) {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onCreateConversation = {
                    // Create a new conversation and navigate to chat
                    val newConversationId = "new_conversation_${System.currentTimeMillis()}"
                    navController.navigate(Routes.CHAT.replace("{conversationId}", newConversationId))
                },
                onShowArchived = {
                    navController.navigate(Routes.ARCHIVED_CONVERSATIONS)
                },
                modifier = modifier
            )
        }
        
        composable(Routes.CHAT) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = conversationId,
                onBackClicked = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        
        composable(Routes.ARCHIVED_CONVERSATIONS) {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onCreateConversation = {
                    val newConversationId = "new_conversation_${System.currentTimeMillis()}"
                    navController.navigate(Routes.CHAT.replace("{conversationId}", newConversationId))
                },
                onShowArchived = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}
