package com.example.gemini_lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gemini_lite.DB.ChatViewModelFactory
import com.example.gemini_lite.DB.MessageDatabase
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.LoginScreen
import com.example.gemini_lite.common.isLoggedIn
import com.example.gemini_lite.navigationDrawer.AppDrawer
import com.example.gemini_lite.ui.theme.Gemini_liteTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: MessageDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            database = MessageDatabase.getInstance(applicationContext)
            val viewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(database.messageDao())
            )
            Gemini_liteTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val loggedInState by isLoggedIn(context).collectAsState(initial = false)

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (loggedInState) "home_screen" else "login_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Login Screen
                        composable("login_screen") {
                            LoginScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }

                        // Home Screen with App Drawer
                        composable("home_screen") {
                            AppDrawer(
                                navController = navController,
                                viewModel = viewModel,
                                onDestinationClicked = { destination ->
                                    if (destination == "Logout") {
                                        viewModel.handleLogout(context)
                                        viewModel.clearChatHistory()
                                        navController.navigate("login_screen") {
                                            popUpTo("home_screen") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
