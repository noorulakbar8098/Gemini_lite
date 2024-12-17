package com.example.gemini_lite.common

import androidx.room.TypeConverter
import com.example.gemini_lite.DB.Messages
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    // Convert Messages object to a JSON String
    @TypeConverter
    fun fromMessages(messages: Messages?): String {
        return gson.toJson(messages)
    }

    // Convert JSON String back to Messages object
    @TypeConverter
    fun toMessages(json: String?): Messages {
        val type = object : TypeToken<Messages>() {}.type
        return gson.fromJson(json, type)
    }
}