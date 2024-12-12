package com.example.gemini_lite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val messageModel: String,
    val role: String
)
