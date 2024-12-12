package com.example.gemini_lite.chatScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@SuppressLint("NewApi")
@Composable
fun ChatHomeScreen(viewModel: ChatViewModel, openDrawer: () -> Unit) {
    val context = LocalContext.current
    val userDetail by viewModel.userDetails.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF181A1C))
        ) {
            // Top Bar with Shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp, // Adjust shadow elevation
                        shape = RectangleShape, // Top bar typically uses a rectangular shape
                        ambientColor = Color.Blue.copy(alpha = 0.2f), // Subtle black shadow
                        spotColor = Color.Green.copy(alpha = 0.3f)
                    )
                    .background(Color(0xFF181A1C)) // Match top bar background color
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Menu Icon",
                        tint = Color.White,
                        modifier = Modifier.clickable { openDrawer() }
                    )

                    Text(
                        text = "Gemini",
                        color = Color.White,
                        style = TextStyle(
                            fontWeight = FontWeight.W400,
                            fontSize = 20.sp
                        )
                    )


                    AsyncImage(
                        model = userDetail?.profileUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Transparent, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            ChatScreen(viewModel)
        }
    }

}