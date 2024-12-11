package com.example.gemini_lite.chatScreen

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini_lite.MessageModel
import com.example.gemini_lite.R
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3AEDFF),
            Color(0xFF8227FA),
            Color(0xFFFF5757)
        )
    )
    var text by remember { mutableStateOf("") }
    var debouncedText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(text) {
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
        if (viewModel.messageList.isEmpty()) {
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
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        } else {
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList)
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 16.dp, end = 16.dp)
                .shadow(
                    elevation = 9.dp, // Elevation for the shadow
                    shape = RoundedCornerShape(16.dp), // Same shape as Surface
                    ambientColor = Color.White, // White shadow color
                    spotColor = Color.White // Additional white shadow color for spot light
                ),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF333333)
        ) {
            Column {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it  },
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
                            viewModel.sendMessage(text, context)
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
                    IconButton(
                        onClick = { /* Handle camera click */ },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

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
                                viewModel.sendMessage(text, context)
                                text = ""
                                keyboardController?.hide()
                                      },
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send, // Send arrow icon
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
    modifier: Modifier,
    messageList: List<MessageModel>
) {
    LazyColumn(
        modifier.padding(10.dp),
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageRow(messageModel = it)
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.fillMaxWidth() ) {
            Box(modifier = Modifier
                .align(
                    if (isModel) Alignment.BottomStart else Alignment.BottomEnd
                )
                .padding(
                    start = if (isModel) 8.dp else 70.dp,
                    end = if (isModel) 70.dp else 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .clip(RoundedCornerShape(50f))
                .background(if (isModel) Color(0xFF333333) else Color.White)
                .padding(15.dp),

            ) {
                Column {
                    if (isModel) {
                        Image(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp),
                            painter = painterResource(id = R.drawable.gemini),
                            contentDescription = "Person",
                        )
                    } else {
                        Icon(
                            modifier = Modifier.clip(CircleShape)
                                .background(Color.White).padding(5.dp),
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Person",
                        )
                    }

                    SelectionContainer {
                        Text(text = messageModel.messageModel,
                            modifier = Modifier.padding(top = 10.dp),
                            color = if (isModel) Color.White else Color.Black,
                            fontWeight = FontWeight.W500)
                    }

                }

            }
        }
    }
}
