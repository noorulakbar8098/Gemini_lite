package com.example.gemini_lite.chatScreen

import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemini_lite.MessageModel
import com.example.gemini_lite.R
import com.example.gemini_lite.common.DotsTyping
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val context = LocalContext.current

    val messageList by viewModel.messageList.collectAsState()
    val userDetail by viewModel.userDetails.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3AEDFF),
            Color(0xFF8227FA),
            Color(0xFFFF5757)
        )
    )
    var text by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var debouncedText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }
    val listener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                isRecording = false
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        Toast.makeText(
                            context,
                            "No speech detected, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        Toast.makeText(
                            context,
                            "Speech timeout, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_NETWORK -> {
                        Toast.makeText(
                            context,
                            "Network error, check your connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        Toast.makeText(
                            context,
                            "Network timeout, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_AUDIO -> {
                        Toast.makeText(
                            context,
                            "Audio error, please check your microphone.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_CLIENT -> {
                        Toast.makeText(
                            context,
                            "Client error, please restart the app.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        Toast.makeText(context, "Recognizer busy, please wait.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        Toast.makeText(
                            context,
                            "Microphone permissions required.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        isRecording = false
                        Toast.makeText(context, "Unknown error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                data?.firstOrNull()?.let {
                    text = it
                    isRecording = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(listener)
    }
    LaunchedEffect(text) {
        viewModel.loadMessages()
        delay(300)
        debouncedText = text
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(Color(0xFF181A1C)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (messageList.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                text = "What can I help with?",
                style = TextStyle(
                    brush = gradient,
                    fontWeight = FontWeight.W500,
                    fontSize = 26.sp
                ),
                textAlign = TextAlign.Center
            )
        } else {
            MessageList(
                isTyping = isTyping,
                userDetail = userDetail,
                modifier = Modifier.weight(1f),
                messageList = messageList
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 16.dp, end = 16.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Green, // White shadow color
                    spotColor = Color.White // Additional white shadow color for spot light
                ),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF333333)
        ) {
            Column {
                val infiniteTransition = rememberInfiniteTransition(label = "")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = ""
                )
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .clickable(enabled = false) {} // Block interactions
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.mic),
                                contentDescription = "Recording",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(scale) // Apply pulsing animation
                            )
                            Spacer(modifier = Modifier.height(36.dp))
                            Text(
                                text = "Listening...",
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.scale(scale),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, // Set the action button to "Done"
                        keyboardType = KeyboardType.Text // Set input type
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.sendMessage(text)
                            text = ""
                        }
                    ),
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(vertical = 8.dp)
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "Ask Gemini",
                                    color = Color.LightGray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(top = 15.dp)
                            .clickable {
                                isRecording = true
                                speechRecognizer.startListening(recognizerIntent)

                            }
                            .size(18.dp),
                        painter = painterResource(id = R.drawable.mic),
                        contentDescription = "Person",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    IconButton(
                        onClick = { text = "" },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.sendMessage(text)
                                text = ""
                                keyboardController?.hide()
                            },
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Filled.Send,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageList(
    isTyping: Boolean,
    userDetail: UserDetails?,
    modifier: Modifier,
    messageList: List<MessageModel>
) {
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
                userDetail = userDetail,
                messageModel = it
            )
        }
        if (isTyping) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DotsTyping()
                }
            }
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

@Composable
fun MessageRow(userDetail: UserDetails?, messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(
                        if (isModel) Alignment.BottomStart else Alignment.BottomEnd
                    )
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color.White,
                        spotColor = Color.White
                    )
                    .clip(RoundedCornerShape(50f))
                    .background(if (isModel) Color(0xFF333333) else Color.LightGray)
                    .padding(15.dp),

                ) {
                if (isModel) {
                    Column {
                        Image(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp),
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Person",
                        )

                        SelectionContainer {
                            Text(
                                text = messageModel.messageModel,
                                modifier = Modifier.padding(top = 10.dp),
                                color = if (isModel) Color.White else Color.Black,
                                fontWeight = FontWeight.W500
                            )
                        }
                    }
                } else {
                    Row {
                        AsyncImage(
                            model = userDetail?.profileUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Transparent, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        SelectionContainer {
                            Text(
                                text = messageModel.messageModel,
                                modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                                color = if (isModel) Color.White else Color.Black,
                                fontWeight = FontWeight.W500
                            )
                        }
                    }
                }
            }
        }
    }
}
