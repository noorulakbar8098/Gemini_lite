package com.example.gemini_lite.DB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gemini_lite.chatScreen.ChatViewModel

class ChatViewModelFactory(private val dao: MessageDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(MessageRepository(dao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
