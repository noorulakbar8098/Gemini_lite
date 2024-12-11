package com.example.gemini_lite

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.gemini_lite.chatScreen.ChatScreen
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.LoginScreen
import com.example.gemini_lite.common.GoogleSignInUtils
import com.example.gemini_lite.common.clearLoginState
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
                                LoginScreen(modifier = Modifier.padding(innerPadding))
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