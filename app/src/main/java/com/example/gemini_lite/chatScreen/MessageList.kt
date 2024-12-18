package com.example.gemini_lite.chatScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gemini_lite.DB.Messages
import com.example.gemini_lite.DB.ProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun MessageList(
    userDetail: ProfileData?,
    modifier: Modifier,
    messageList: List<Messages>,
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val listState = rememberLazyListState()
    val showArrow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier.padding(10.dp),
        reverseLayout = true,
        state = listState
    ) {
        items(messageList.reversed()) {
            MessageRow(
                profileImageUri = profileImageUri,
                userDetail = userDetail,
                messageModel = it,
                onSoundIconClick = {
                    val selectedLanguage = MutableStateFlow("en")
                    viewModel.speakResponse(it, selectedLanguage.value)
                },
                onVideoClick = { videoUrl -> openYouTubeVideo(context, videoUrl) }
            )
        }
    }

    if (showArrow) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Padding to keep the icon above the screen's bottom edge
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = 0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter), // Center the button horizontally
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll to bottom",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun openYouTubeVideo(context: Context, videoUrl: String) {
    if (Patterns.WEB_URL.matcher(videoUrl).matches() &&
        (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be"))) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        intent.setPackage("com.google.android.youtube") // Explicitly target YouTube app

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to browser if YouTube app is not installed
            intent.setPackage(null)
            context.startActivity(intent)
        }
    } else {
        // Handle invalid or non-YouTube URLs
        Toast.makeText(context, "Invalid YouTube video URL.", Toast.LENGTH_SHORT).show()
    }
}


