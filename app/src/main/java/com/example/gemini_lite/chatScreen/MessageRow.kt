package com.example.gemini_lite.chatScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gemini_lite.DB.Messages
import com.example.gemini_lite.DB.ProfileData
import com.example.gemini_lite.R
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.Files.append

@Composable
fun MessageRow(
    profileImageUri: String?,
    userDetail: ProfileData?,
    messageModel: Messages,
    onSoundIconClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    val isModel = messageModel.role == "model"
    val context = LocalContext.current
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
                        when {
                            isValidVideoUrl(messageModel.messageModel) -> {
                                // Extract the YouTube video ID
                                val videoId = extractYouTubeVideoId(messageModel.messageModel)
                                val thumbnailUrl = videoId?.let { getYouTubeThumbnailUrl(it) }

                                // Display a clickable video thumbnail or fallback
                                Box(
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black)
                                        .clickable { onVideoClick(messageModel.messageModel) }
                                ) {
                                    if (thumbnailUrl != null) {
                                        AsyncImage(
                                            model = thumbnailUrl,
                                            contentDescription = "YouTube Thumbnail",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.play), // Play icon
                                            contentDescription = "Play Icon",
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(50.dp),
                                            tint = Color.White
                                        )
                                    } else {
                                        // Fallback for invalid YouTube video URL
                                        Text(
                                            text = "Play Video",
                                            color = Color.White,
                                            modifier = Modifier.align(Alignment.Center),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            isValidImageUrl(messageModel.messageModel) -> {
                                // Display image if it's an image URL
                                AsyncImage(
                                    model = messageModel.messageModel,
                                    contentDescription = "Generated Image",
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                // Display text
                                SelectionContainer {
                                    val annotatedText = buildAnnotatedString {
                                        val message = messageModel.messageModel

                                        // Regular expression to detect URLs
                                        val urlRegex = Regex("(https?://[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=]*)?)")

                                        var lastIndex = 0
                                        urlRegex.findAll(message).forEach { match ->
                                            val start = match.range.first
                                            val end = match.range.last + 1
                                            append(message.substring(lastIndex, start))
                                            pushStringAnnotation(tag = "URL", annotation = match.value)
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color(0xFF1E90FF),
                                                    textDecoration = TextDecoration.Underline)
                                            ) {
                                                append(match.value)
                                            }
                                            pop()

                                            lastIndex = end
                                        }

                                        // Add any remaining text after the last URL
                                        if (lastIndex < message.length) {
                                            append(message.substring(lastIndex))
                                        }
                                    }

                                    ClickableText(
                                        text = annotatedText,
                                        modifier = Modifier.padding(top = 10.dp),
                                        style = TextStyle(color = Color.White, fontWeight = FontWeight.W500, textAlign = TextAlign.Start),
                                        onClick = { offset ->
                                            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                                .firstOrNull()?.let { annotation ->
                                                    onLinkClick(context,annotation.item) // Open the URL
                                                }
                                        }
                                    )
                                }
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

fun isValidVideoUrl(url: String): Boolean {
    return url.contains("youtube.com") || url.contains("youtu.be") || url.contains(".mp4")
}

fun extractYouTubeVideoId(url: String): String? {
    val regex = Regex("(?:youtube\\.com.*(?:\\?|&)v=|youtu\\.be/)([^&\\n?#]+)")
    return regex.find(url)?.groupValues?.get(1)
}

fun getYouTubeThumbnailUrl(videoId: String): String {
    return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
}

fun onLinkClick(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}


