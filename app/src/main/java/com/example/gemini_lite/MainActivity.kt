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
import androidx.lifecycle.ViewModelProvider
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.LoginScreen
import com.example.gemini_lite.common.isLoggedIn
import com.example.gemini_lite.navigationDrawer.AppDrawer
import com.example.gemini_lite.ui.theme.Gemini_liteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: ChatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            Gemini_liteTheme {
                val context = LocalContext.current
                val loggedInState by isLoggedIn(context).collectAsState(initial = false)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (loggedInState) {
                        AppDrawer(viewModel, onDestinationClicked = { destination ->
                            if (destination == "Logout") {
                                viewModel.handleLogout(context)
//                                LoginScreen(modifier = Modifier.padding(innerPadding))
                            }
                        })
                    } else {
                        LoginScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}