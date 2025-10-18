package com.example.chatai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.chatai.presentation.ChatAiApp
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