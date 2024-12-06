package com.example.gemini_lite

import android.content.Context
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini_lite.constants.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    var messageInput by mutableStateOf(MessageData())
        private set
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    val text by lazy { mutableStateOf("") }
    val generateModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(message: String, context: Context) {
        viewModelScope.launch {
            try {
                val chat = generateModel.startChat(
                    history = messageList.map {
                        content(it.role){ text(it.messageModel) }
                    }.toList()
                )

                Log.e("sendMessage", message.toString())
                messageList.add(MessageModel(message, "user"))
                messageList.add(MessageModel("Generating...", "model"))
                val response = chat.sendMessage(message)
                if (messageList.isNotEmpty()) {
                    messageList.removeAt(messageList.size - 1)
                }
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e: Exception) {
                if (messageList.isNotEmpty()) {
                    messageList.removeAt(messageList.size - 1)
                }
                messageList.add(MessageModel("Generating... + ${e.message}", "model"))
            }

        }

    }
}

data class MessageData(
    val text: String? = ""
)