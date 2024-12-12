package com.example.gemini_lite.chatScreen

import android.content.Context
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini_lite.DB.MessageRepository
import com.example.gemini_lite.MessageModel
import com.example.gemini_lite.common.clearLoginState
import com.example.gemini_lite.common.constants.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val repository: MessageRepository) : ViewModel() {
    private val _messageList = MutableStateFlow<List<MessageModel>>(emptyList())
    val messageList: StateFlow<List<MessageModel>> = _messageList.asStateFlow()

    private val _isTyping = MutableStateFlow(false) // Tracks typing state
    val isTyping: StateFlow<Boolean> get() = _isTyping

    private val generateModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails.asStateFlow()

    fun saveUserDetails(name: String, email: String, photoUrl: String) {
        _userDetails.value = UserDetails(name, email, photoUrl)
    }

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _messageList.value = withContext(Dispatchers.IO) {
                repository.getAllMessages()
            }
        }
    }


    fun clearChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearMessages()
            _messageList.value = emptyList()
        }
    }

    fun handleLogout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            clearLoginState(context)
        }
    }


    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                val userMessage = MessageModel(
                    messageModel = message,
                    role = "user"
                )
                _messageList.value += userMessage
                withContext(Dispatchers.IO) {
                    repository.insertMessage(userMessage)
                }
                val generatingMessage = MessageModel(
                    messageModel = "Generating...",
                    role = "user"
                )
                _messageList.value += generatingMessage
                val chat = generateModel.startChat(
                    history = _messageList.value.map {
                        content(it.role) { text(it.messageModel) }
                    }
                )
                val response = chat.sendMessage(message)
                val responseMessage = MessageModel(
                    messageModel = response.text.toString().replace("*", ""),
                    role = "model"
                )
                _messageList.value += responseMessage
                withContext(Dispatchers.IO) {
                    repository.insertMessage(responseMessage)
                }
            } catch (e: Exception) {
                _messageList.value = _messageList.value.dropLast(1) // Remove placeholder
                val errorMessage = MessageModel(
                    messageModel = "Error: ${e.message}",
                    role = "model"
                )
                _messageList.value += errorMessage // Update UI
            } finally {
                _isTyping.value = false
            }
        }
    }
}

data class UserDetails(
    val name: String? = "",
    val email: String? = "",
    val profileUrl: String? = ""
)