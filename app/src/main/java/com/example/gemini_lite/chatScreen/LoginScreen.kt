package com.example.gemini_lite.chatScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.gemini_lite.R
import com.example.gemini_lite.common.GoogleSignInUtils
import com.example.gemini_lite.common.isLoggedIn

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
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
                login = {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(brush = gradient)) {
                            append(stringResource(id = R.string.app_name))
                        }
                    },
                    fontSize = 48.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(start = 16.dp, top = 100.dp)
                )

                // Example Gemini text
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = {
                        GoogleSignInUtils.doGoogleSignIn(
                            context = context,
                            scope = scope,
                            launcher = launcher,
                            login = {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(34.dp)
                        )
                        Text(text = "Google Sign in", color = Color.Black)
                    }
                }
            }
        }
    }
}