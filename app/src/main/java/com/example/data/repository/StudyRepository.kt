package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.StudyBlockRecord
import com.example.data.model.StudySession
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StudyRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val sessionDao = database.studySessionDao()

    val allUsers: Flow<List<UserProfile>> = userDao.getAllUsers()

    suspend fun ensureDefaultUser(): UserProfile {
        val users = userDao.getAllUsers().first()
        if (users.isNotEmpty()) {
            return users.first()
        }
        val defaultUser = UserProfile(
            name = "Estudiante Focus",
            colorHex = "#E04F36",
            dailyGoalMinutes = 100,
            avatarEmoji = "🚀"
        )
        val id = userDao.insertUser(defaultUser)
        return defaultUser.copy(id = id)
    }

    suspend fun createUser(name: String, colorHex: String, dailyGoalMinutes: Int, emoji: String): Long {
        return userDao.insertUser(
            UserProfile(
                name = name,
                colorHex = colorHex,
                dailyGoalMinutes = dailyGoalMinutes,
                avatarEmoji = emoji
            )
        )
    }

    suspend fun updateUser(user: UserProfile) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserProfile) {
        userDao.deleteUser(user)
    }

    fun getSessionsForUser(userId: Long): Flow<List<StudySession>> {
        return sessionDao.getSessionsForUser(userId)
    }

    suspend fun saveCompletedSession(
        session: StudySession,
        blocks: List<StudyBlockRecord>
    ): Long {
        val sessionId = sessionDao.insertSession(session)
        val linkedBlocks = blocks.map { it.copy(sessionId = sessionId) }
        sessionDao.insertBlocks(linkedBlocks)
        return sessionId
    }

    fun getBlocksForSession(sessionId: Long): Flow<List<StudyBlockRecord>> {
        return sessionDao.getBlocksForSession(sessionId)
    }

    suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSessionById(sessionId)
    }
}
