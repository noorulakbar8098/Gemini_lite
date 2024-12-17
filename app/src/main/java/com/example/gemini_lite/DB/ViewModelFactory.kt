package com.example.gemini_lite.DB

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gemini_lite.chatScreen.ChatViewModel

class ChatViewModelFactory(private val dao: MessageDao, private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(MessageRepository(dao), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
