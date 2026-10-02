package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val topic: String,
    val subtopic: String,
    val totalBlocksPlanned: Int,
    val blocksCompleted: Int,
    val workDurationMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val totalFocusSecondsSpent: Long,
    val totalBreakSecondsSpent: Long,
    val startTime: Long,
    val endTime: Long,
    val isCompleted: Boolean = true,
    val notes: String = "",
    val satisfactionRating: Int = 5 // 1 to 5 stars
)
