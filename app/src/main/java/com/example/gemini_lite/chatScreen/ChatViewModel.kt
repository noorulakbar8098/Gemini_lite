package com.example.gemini_lite.chatScreen

import android.content.Context
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini_lite.MessageModel
import com.example.gemini_lite.common.clearLoginState
import com.example.gemini_lite.common.constants.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    private val generateModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    fun handleLogout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            clearLoginState(context)
        }
    }

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