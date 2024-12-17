package com.example.gemini_lite.DB

class MessageRepository(private val dao: MessageDao) {

    suspend fun getOrInsertChatHistory(sessionId: Long): Long {
        val existingChatHistory = dao.getChatHistoryBySessionId(sessionId)
        return if (existingChatHistory != null) {
            existingChatHistory.id
        } else {
            val newChatHistory = ChatHistory(
                id = sessionId,
                title = "default"
            )
            dao.insertChatHistory(newChatHistory)
        }
    }

    suspend fun insertMessage(message: Messages) {
        dao.insertMessage(message)
    }

    suspend fun getMessagesForHistory(chatHistoryId: Long): List<Messages> {
        return dao.getMessagesForChatHistory(chatHistoryId)
    }

    suspend fun getALlMessages(): List<Messages> {
        return dao.getAllMessages()
    }


    fun getProfileData(): ProfileData {
        return dao.getProfileDetails()
    }

    fun clearMessages() {
        dao.clearMessageList()
    }

    fun clearChatHistory() {
        dao.clearChatHistory()
    }

    fun clearLoginCred() {
        dao.clearProfile()
    }

    fun insertProfileDetails(profile: ProfileData) {
        dao.insertProfileDetails(profile)
    }

    suspend fun updateProfileUrl(id: Int, photoUrl: String) {
        dao.updateProfileUrl(id, photoUrl)
    }


    suspend fun getProfileUrl(profileId: Int): String? {
        return dao.getProfileUrl(profileId)
    }
}

