package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudyBlockRecord
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY endTime DESC")
    fun getSessionsForUser(userId: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions ORDER BY endTime DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Update
    suspend fun updateSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<StudyBlockRecord>)

    @Query("SELECT * FROM study_blocks WHERE sessionId = :sessionId ORDER BY blockIndex ASC")
    fun getBlocksForSession(sessionId: Long): Flow<List<StudyBlockRecord>>

    @Update
    suspend fun updateBlock(block: StudyBlockRecord)

    @Query("DELETE FROM study_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)
}
