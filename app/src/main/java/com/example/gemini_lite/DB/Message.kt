package com.example.gemini_lite.DB

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatHistory::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class Messages(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unique message ID
    var sessionId: Long, // Foreign key linking to ChatHistory
    val messageModel: String, // The message content
    val role: String, // "user" or "model"
    val timestamp: Long = System.currentTimeMillis() // Message timestamp
)
