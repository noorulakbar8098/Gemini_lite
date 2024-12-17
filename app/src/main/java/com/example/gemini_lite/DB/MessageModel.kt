package com.example.gemini_lite.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val messageModel: String,
    val role: String
)
