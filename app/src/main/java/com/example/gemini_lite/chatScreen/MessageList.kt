package com.example.gemini_lite.chatScreen

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
                }
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