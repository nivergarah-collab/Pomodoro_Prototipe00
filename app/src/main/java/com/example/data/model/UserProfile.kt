package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#E04F36",
    val dailyGoalMinutes: Int = 100,
    val avatarEmoji: String = "🎓",
    val createdAt: Long = System.currentTimeMillis()
)
