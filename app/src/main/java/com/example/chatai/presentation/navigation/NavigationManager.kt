package com.example.chatai.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatai.presentation.screens.ApiKeySetupScreen
import com.example.chatai.presentation.screens.ChatScreen
import com.example.chatai.presentation.screens.ConversationListScreen
import com.example.chatai.presentation.screens.DefaultModelSettingsScreen
import com.example.chatai.presentation.screens.ExportConversationScreen
import com.example.chatai.presentation.screens.ModelComparisonScreen
import com.example.chatai.presentation.screens.OnboardingScreen
import com.example.chatai.presentation.screens.SplashScreen
import com.example.chatai.presentation.screens.ThemeSettingsScreen

// Navigation routes
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val API_KEY_SETUP = "api_key_setup"
    const val CONVERSATION_LIST = "conversation_list"
    const val CHAT = "chat/{conversationId}"
    const val ARCHIVED_CONVERSATIONS = "archived_conversations"
    const val THEME_SETTINGS = "theme_settings"
    const val DEFAULT_MODEL_SETTINGS = "default_model_settings"
    const val EXPORT_CONVERSATION = "export_conversation/{conversationId}"
    const val MODEL_COMPARISON = "model_comparison/{conversationId}"
}

@Composable
fun NavigationManager(
    navController: NavHostController,
    onContinueClicked: () -> Unit,
    onApiKeyConfigured: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(modifier = modifier)
        }
        
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onContinueClicked = onContinueClicked,
                modifier = modifier
            )
        }
        
        composable(Routes.API_KEY_SETUP) {
            ApiKeySetupScreen(
                onApiKeyConfigured = onApiKeyConfigured,
                modifier = modifier
            )
        }
        
        composable(Routes.CONVERSATION_LIST) {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onCreateConversation = { conversationId ->
                    // Navigate to chat with the real conversation ID
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onShowArchived = {
                    navController.navigate(Routes.ARCHIVED_CONVERSATIONS)
                },
                onNavigateToThemeSettings = {
                    navController.navigate(Routes.THEME_SETTINGS)
                },
                onNavigateToDefaultModelSettings = {
                    navController.navigate(Routes.DEFAULT_MODEL_SETTINGS)
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
                onNavigateToExportConversation = { 
                    navController.navigate(Routes.EXPORT_CONVERSATION.replace("{conversationId}", conversationId))
                },
                onNavigateToModelComparison = {
                    navController.navigate(Routes.MODEL_COMPARISON.replace("{conversationId}", conversationId))
                },
                modifier = modifier
            )
        }
        
        composable(Routes.ARCHIVED_CONVERSATIONS) {
            ConversationListScreen(
                onConversationClick = { conversationId ->
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onCreateConversation = { conversationId ->
                    // Navigate to chat with the real conversation ID
                    navController.navigate(Routes.CHAT.replace("{conversationId}", conversationId))
                },
                onShowArchived = {
                    navController.popBackStack()
                },
                onNavigateToThemeSettings = {
                    navController.navigate(Routes.THEME_SETTINGS)
                },
                onNavigateToDefaultModelSettings = {
                    navController.navigate(Routes.DEFAULT_MODEL_SETTINGS)
                },
                modifier = modifier
            )
        }
        
        composable(Routes.THEME_SETTINGS) {
            ThemeSettingsScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        
        composable(Routes.DEFAULT_MODEL_SETTINGS) {
            DefaultModelSettingsScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        
        composable(Routes.EXPORT_CONVERSATION) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ExportConversationScreen(
                conversationId = conversationId,
                onBackClicked = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        
        composable(Routes.MODEL_COMPARISON) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ModelComparisonScreen(
                conversationId = conversationId,
                onBackClicked = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}
