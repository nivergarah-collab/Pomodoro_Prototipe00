package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_blocks",
    foreignKeys = [
        ForeignKey(
            entity = StudySession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class StudyBlockRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long = 0,
    val blockIndex: Int,
    val title: String,
    val goal: String = "",
    val durationMinutes: Int = 25,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)
