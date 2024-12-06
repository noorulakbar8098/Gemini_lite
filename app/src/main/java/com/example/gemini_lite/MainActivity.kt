package com.example.gemini_lite

import android.graphics.fonts.FontStyle
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.gemini_lite.ui.theme.Gemini_liteTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
            val gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF3A7DFF), // Blue
                    Color(0xFF8757FF), // Purple
                    Color(0xFFFF5757)  // Red
                )
            )
            Gemini_liteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) { // Use Box to position elements on top of each other
                        // Column for other content like ChatScreen
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(Color(0xFF333333))
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth() // Make the Row fill the top of the screen
                                    .padding(15.dp), // Add padding around the Row
                                horizontalArrangement = Arrangement.SpaceBetween, // Space out the children (Text and Image)
                                verticalAlignment = Alignment.CenterVertically // Align children vertically centered
                            ) {
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
//                                        .graphicsLayer(scaleX = 1.6f, scaleY = 1.6f),
                                    painter = painterResource(id = R.drawable.gemini),
                                    contentDescription = "Person",
                                    contentScale = ContentScale.Crop// Ensures the image scales appropriately within the CircleShape
                                )
                            }
                            ChatScreen(viewModel)
                        }

                    }
                }


            }
        }
    }
}