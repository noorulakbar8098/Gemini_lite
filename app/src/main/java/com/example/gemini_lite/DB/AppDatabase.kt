package com.example.gemini_lite.DB

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gemini_lite.MessageModel

@Database(entities = [MessageModel::class], version = 1)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "message_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

