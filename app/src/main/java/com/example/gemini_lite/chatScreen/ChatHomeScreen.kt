package com.example.gemini_lite.chatScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemini_lite.R

@SuppressLint("NewApi")
@Composable
fun ChatHomeScreen(viewModel: ChatViewModel, openDrawer: () -> Unit) {
    val userDetail by viewModel.profileData.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxWidth()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFF181A1C))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        painter = painterResource(id = R.drawable.chatting),
                        contentDescription = "Menu Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable { openDrawer() }
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
                        model = profileImageUri ?: userDetail.profileUrl,
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