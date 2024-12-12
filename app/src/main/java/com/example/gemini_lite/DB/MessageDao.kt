package com.example.gemini_lite.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gemini_lite.MessageModel

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getAllMessages(): List<MessageModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageModel)

    @Delete
    fun deleteMessage(message: MessageModel)

//    @Query("DELETE FROM messages")
//    fun clearAllMessages(): List<MessageModel>
}
