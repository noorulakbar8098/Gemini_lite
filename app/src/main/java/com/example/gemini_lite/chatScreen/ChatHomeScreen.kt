package com.example.gemini_lite.chatScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini_lite.R

@SuppressLint("NewApi")
@Composable
fun ChatHomeScreen(viewModel: ChatViewModel, openDrawer: () -> Unit) {
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF333333))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val gradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3A7DFF), // Blue
                        Color(0xFF8757FF), // Purple
                        Color(0xFFFF5757)  // Red
                    )
                )

                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu Icon",
                    tint = Color.White,
                    modifier = Modifier.clickable { openDrawer() }
                )

                Text(
                    text = "Gemini ®",
                    style = TextStyle(
                        brush = gradient,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    )
                )

                Image(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50f))
                        .size(40.dp),
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop
                )
            }

            ChatScreen(viewModel)

            Button(
                onClick = {
                    viewModel.handleLogout(context)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}