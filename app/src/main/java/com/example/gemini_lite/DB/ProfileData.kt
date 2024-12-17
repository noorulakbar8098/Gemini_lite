package com.example.gemini_lite.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var email: String = "",
    var profileUrl: String = "",
)