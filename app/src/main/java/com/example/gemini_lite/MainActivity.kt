package com.example.gemini_lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gemini_lite.DB.ChatViewModelFactory
import com.example.gemini_lite.DB.MessageDatabase
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.LoginScreen
import com.example.gemini_lite.common.dataStore
import com.example.gemini_lite.common.isLoggedIn
import com.example.gemini_lite.navigationDrawer.HomeScreen
import com.example.gemini_lite.ui.theme.Gemini_liteTheme
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    private lateinit var database: MessageDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = MessageDatabase.getInstance(applicationContext)
        setContent {
            val context = LocalContext.current
            val loggedInState by isLoggedIn(context).collectAsState(initial = false) // Observe login state
            val startDestination = if (loggedInState) "home_screen" else "login_screen"
            val viewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(database.messageDao(), this)
            )
            Gemini_liteTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login_screen") {
                            LoginScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }

                        composable("home_screen") {
                            HomeScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}







