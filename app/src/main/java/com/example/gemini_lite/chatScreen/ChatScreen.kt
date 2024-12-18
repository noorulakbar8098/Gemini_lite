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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.gemini_lite.R
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val sessionId by viewModel.sessionId.collectAsState()
    val messageList by viewModel.messageList.collectAsState()
    val userDetail by viewModel.profileData.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3AEDFF),
            Color(0xFF8227FA),
            Color(0xFFFF5757)
        )
    )

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
                userDetail = userDetail,
                modifier = Modifier.weight(1f),
                messageList = messageList,
                viewModel = viewModel
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp, start = 16.dp, end = 16.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Green,
                    spotColor = Color.White
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
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .imePadding(),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, // Set the action button to "Done"
                        keyboardType = KeyboardType.Text // Set input type
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (text.isNotEmpty() && text.trim().length > 4) {
                            if (sessionId != null) {
                                viewModel.sendMessage(sessionId, text)
                            } else {
                                viewModel.sendMessage(0, text)
                            }
                        }
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
                    if (text.isNotEmpty() && text.trim().length > 4) {
                        IconButton(
                            onClick = {
                                if (sessionId != null && sessionId > 1) {
                                    viewModel.sendMessage(sessionId, text)
                                } else {
                                    viewModel.sendMessage(0, text)
                                }
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


fun isValidImageUrl(url: String): Boolean {
    return url.startsWith("http", ignoreCase = true) &&
            (url.endsWith(".jpg", ignoreCase = true) ||
                    url.endsWith(".jpeg", ignoreCase = true) ||
                    url.endsWith(".png", ignoreCase = true) ||
                    url.endsWith(".gif", ignoreCase = true) ||
                    url.endsWith(".webp", ignoreCase = true))
}
