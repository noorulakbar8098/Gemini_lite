package com.example.gemini_lite.DB

import com.example.gemini_lite.MessageModel

class MessageRepository(private val dao: MessageDao) {

    suspend fun insertMessage(message: MessageModel) {
        dao.insertMessage(message)
    }

    suspend fun getAllMessages(): List<MessageModel> {
        return dao.getAllMessages()
    }

    suspend fun clearMessages() {
//        dao.clearAllMessages()
    }
}

