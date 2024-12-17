package com.example.gemini_lite.chatScreen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gemini_lite.R
import com.example.gemini_lite.common.GoogleSignInUtils
import com.example.gemini_lite.common.isLoggedIn

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val loggedInState by isLoggedIn(context).collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    if (!loggedInState) {
        val gradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF3A7DFF), // Blue
                Color(0xFF8757FF), // Purple
                Color(0xFFFF5757)  // Red
            )
        )
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            GoogleSignInUtils.doGoogleSignIn(
                context = context,
                scope = scope,
                launcher = null,
                login = { id, userName, userEmail, userProfilePic ->
                    if (userName != null && userEmail != null) {
                        if (userProfilePic != null) {
                            viewModel.saveUserDetails(id, userName, userEmail, userProfilePic)
                        }
                        navController.navigate("home_Screen") {
                            popUpTo("login_screen") { inclusive = true }
                        }
                    } else {
                        Log.e("GoogleSignIn", "Failed to fetch user details")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(brush = gradient)) {
                            append(stringResource(id = R.string.app_name))
                        }
                    },
                    fontSize = 48.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(start = 20.dp, top = 50.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 40.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    listOf(
                        R.string.gemini_text,
                        R.string.gemini_text2,
                        R.string.gemini_text3,
                        R.string.gemini_text4
                    ).forEach { textRes ->
                        Text(
                            text = stringResource(id = textRes),
                            fontSize = 50.sp,
                            fontWeight = FontWeight.W400,
                            color = Color.Black
                        )
                    }
                    Button(
                        onClick = {
                            GoogleSignInUtils.doGoogleSignIn(
                                context = context,
                                scope = scope,
                                launcher = launcher,
                                login = { id, userName, userEmail, userProfilePic ->
                                    if (userName != null && userEmail != null) {
                                        Log.d("GoogleSignIn", "Logged in User Name: $userName")
                                        Log.d("GoogleSignIn", "Logged in User Email: $userEmail")
                                        if (userProfilePic != null) {
                                            viewModel.saveUserDetails(
                                                id,
                                                userName,
                                                userEmail,
                                                userProfilePic
                                            )
                                        }
                                        navController.navigate("home_Screen") {
                                            popUpTo("login_screen") {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        Log.e("GoogleSignIn", "Failed to fetch user details")
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, end = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Icon",
                                modifier = Modifier.size(38.dp)
                            )
                            Text(
                                text = "Google Sign in",
                                fontSize = 23.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}