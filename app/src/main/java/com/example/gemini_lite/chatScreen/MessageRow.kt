package com.example.gemini_lite.chatScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gemini_lite.DB.Messages
import com.example.gemini_lite.DB.ProfileData
import com.example.gemini_lite.R

@Composable
fun MessageRow(
    profileImageUri: String?,
    userDetail: ProfileData?,
    messageModel: Messages,
    onSoundIconClick: (String) -> Unit
) {
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
                            contentDescription = "Model Icon"
                        )

                        // Check if the response contains an image URL
                        if (isValidImageUrl(messageModel.messageModel)) {
                            AsyncImage(
                                model = messageModel.messageModel, // Use the image URL directly
                                contentDescription = "Generated Image",
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            SelectionContainer {
                                Text(
                                    text = messageModel.messageModel,
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                        if (!isValidImageUrl(messageModel.messageModel)) {
                            IconButton(
                                onClick = { onSoundIconClick(messageModel.messageModel) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.volume),
                                    contentDescription = "Sound Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }


                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = profileImageUri ?: userDetail?.profileUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Transparent, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                        if (isValidImageUrl(messageModel.messageModel)) {
                            AsyncImage(
                                model = messageModel.messageModel,
                                contentDescription = "User Sent Image",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            SelectionContainer {
                                Text(
                                    text = messageModel.messageModel,
                                    modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                                    color = Color.Black,
                                    fontWeight = FontWeight.W500
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}