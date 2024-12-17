package com.example.gemini_lite.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {

    @Query("SELECT * FROM chat_history WHERE id = :sessionId")
    suspend fun getChatHistoryBySessionId(sessionId: Long): ChatHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatHistory(chatHistory: ChatHistory): Long

    @Insert
    suspend fun insertMessage(message: Messages)

    @Query("SELECT * FROM messages WHERE id = :chatHistoryId")
    suspend fun getMessagesForChatHistory(chatHistoryId: Long): List<Messages>

    @Query("SELECT * FROM messages")
    suspend fun getAllMessages(): List<Messages>


    @Query("SELECT profileUrl FROM profile WHERE id = :profileId LIMIT 1")
    suspend fun getProfileUrl(profileId: Int): String?


    @Query("SELECT * FROM profile LIMIT 1")
    fun getProfileDetails(): ProfileData

    @Insert
    fun insertProfileDetails(profile: ProfileData)

    @Query("DELETE FROM messages")
    fun clearMessageList()

    @Query("DELETE FROM chat_history")
    fun clearChatHistory()

    @Query("DELETE FROM profile")
    fun clearProfile()

    @Query("UPDATE profile SET profileUrl = :photoUrl WHERE id = :id")
    suspend fun updateProfileUrl(id: Int, photoUrl: String)
}
