package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.StudyBlockRecord
import com.example.data.model.StudySession
import com.example.data.model.UserProfile
import com.example.data.repository.StudyRepository
import com.example.util.AlertType
import com.example.util.NotificationAlertManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerPhase(val displayName: String) {
    FOCUS("Enfoque"),
    SHORT_BREAK("Descanso Corto"),
    LONG_BREAK("Descanso Largo"),
    FINISHED("Completado")
}

data class ActiveBlockState(
    val index: Int,
    val title: String,
    val goal: String,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

data class SessionSummaryData(
    val topic: String,
    val subtopic: String,
    val totalBlocksPlanned: Int,
    val blocksCompleted: Int,
    val focusSeconds: Long,
    val breakSeconds: Long,
    val completionRate: Float,
    val satisfactionRating: Int,
    val notes: String
)

data class StudyPreset(
    val name: String,
    val description: String,
    val blocks: Int,
    val workMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudyRepository
    private val alertManager = NotificationAlertManager(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudyRepository(db)
    }

    // Sound & Vibration alert preferences
    private val _soundAlertsEnabled = MutableStateFlow(true)
    val soundAlertsEnabled: StateFlow<Boolean> = _soundAlertsEnabled.asStateFlow()

    private val _vibrationAlertsEnabled = MutableStateFlow(true)
    val vibrationAlertsEnabled: StateFlow<Boolean> = _vibrationAlertsEnabled.asStateFlow()

    // Dark Mode Theme preference: null = follow system, true = dark, false = light
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    fun toggleSoundAlerts() {
        _soundAlertsEnabled.update { !it }
    }

    fun toggleVibrationAlerts() {
        _vibrationAlertsEnabled.update { !it }
    }

    fun setSoundAlerts(enabled: Boolean) {
        _soundAlertsEnabled.value = enabled
    }

    fun setVibrationAlerts(enabled: Boolean) {
        _vibrationAlertsEnabled.value = enabled
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun toggleDarkTheme(currentEffectiveDark: Boolean) {
        _isDarkTheme.value = !currentEffectiveDark
    }

    fun testAlert(type: AlertType) {
        alertManager.triggerAlert(type, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)
    }

    // User State
    val allUsers: StateFlow<List<UserProfile>> = MutableStateFlow<List<UserProfile>>(emptyList()).also { flow ->
        viewModelScope.launch {
            repository.allUsers.collectLatest { list ->
                (flow as MutableStateFlow).value = list
                if (list.isEmpty()) {
                    val defaultUser = repository.ensureDefaultUser()
                    _activeUser.value = defaultUser
                } else if (_activeUser.value == null || list.none { it.id == _activeUser.value?.id }) {
                    _activeUser.value = list.first()
                }
            }
        }
    }

    private val _activeUser = MutableStateFlow<UserProfile?>(null)
    val activeUser: StateFlow<UserProfile?> = _activeUser.asStateFlow()

    // Sessions for active user
    private val _userSessions = MutableStateFlow<List<StudySession>>(emptyList())
    val userSessions: StateFlow<List<StudySession>> = _userSessions.asStateFlow()

    init {
        viewModelScope.launch {
            _activeUser.collectLatest { user ->
                if (user != null) {
                    repository.getSessionsForUser(user.id).collectLatest { sessions ->
                        _userSessions.value = sessions
                    }
                } else {
                    _userSessions.value = emptyList()
                }
            }
        }
    }

    // Presets
    val presets = listOf(
        StudyPreset("Clásico Pomodoro", "25 min estudio / 5 min descanso", 4, 25, 5, 15),
        StudyPreset("Deep Work Intensivo", "50 min estudio / 10 min descanso", 3, 50, 10, 20),
        StudyPreset("Sprint Rápido", "15 min estudio / 3 min descanso", 4, 15, 3, 10),
        StudyPreset("Maratón de Estudio", "45 min estudio / 15 min descanso", 6, 45, 15, 25)
    )

    // Configuration State
    var configTopic = MutableStateFlow("Programación Kotlin")
    var configSubtopic = MutableStateFlow("Corrutinas y Flow")
    var configBlocksCount = MutableStateFlow(4)
    var configWorkMinutes = MutableStateFlow(25)
    var configShortBreakMinutes = MutableStateFlow(5)
    var configLongBreakMinutes = MutableStateFlow(15)
    var configBlockGoals = MutableStateFlow<Map<Int, String>>(emptyMap())

    // Active Timer & Session State
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _currentPhase = MutableStateFlow(TimerPhase.FOCUS)
    val currentPhase: StateFlow<TimerPhase> = _currentPhase.asStateFlow()

    private val _currentBlockIndex = MutableStateFlow(1)
    val currentBlockIndex: StateFlow<Int> = _currentBlockIndex.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(25 * 60)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _phaseTotalSeconds = MutableStateFlow(25 * 60)
    val phaseTotalSeconds: StateFlow<Int> = _phaseTotalSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _activeBlocks = MutableStateFlow<List<ActiveBlockState>>(emptyList())
    val activeBlocks: StateFlow<List<ActiveBlockState>> = _activeBlocks.asStateFlow()

    private var accumulatedFocusSeconds = 0L
    private var accumulatedBreakSeconds = 0L
    private var sessionStartTime = 0L
    private var timerJob: Job? = null

    // Summary dialog
    private val _showSummaryDialog = MutableStateFlow(false)
    val showSummaryDialog: StateFlow<Boolean> = _showSummaryDialog.asStateFlow()

    private val _lastSummary = MutableStateFlow<SessionSummaryData?>(null)
    val lastSummary: StateFlow<SessionSummaryData?> = _lastSummary.asStateFlow()

    // Navigation state: 0 = Nueva Sesion, 1 = En Curso, 2 = Estadisticas
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun applyPreset(preset: StudyPreset) {
        configBlocksCount.value = preset.blocks
        configWorkMinutes.value = preset.workMinutes
        configShortBreakMinutes.value = preset.shortBreakMinutes
        configLongBreakMinutes.value = preset.longBreakMinutes
    }

    fun setBlockGoal(blockIndex: Int, goal: String) {
        configBlockGoals.update { current ->
            current + (blockIndex to goal)
        }
    }

    fun startSession() {
        val totalBlocks = configBlocksCount.value
        val workMins = configWorkMinutes.value
        val topic = configTopic.value.ifBlank { "Tema General" }
        val subtopic = configSubtopic.value.ifBlank { "Sesión de Enfoque" }

        val blocks = (1..totalBlocks).map { i ->
            val customGoal = configBlockGoals.value[i] ?: ""
            ActiveBlockState(
                index = i,
                title = "Bloque $i",
                goal = if (customGoal.isNotBlank()) customGoal else "$topic: $subtopic",
                durationMinutes = workMins,
                isCompleted = false
            )
        }

        _activeBlocks.value = blocks
        _currentBlockIndex.value = 1
        _currentPhase.value = TimerPhase.FOCUS
        val initialSeconds = workMins * 60
        _secondsRemaining.value = initialSeconds
        _phaseTotalSeconds.value = initialSeconds
        accumulatedFocusSeconds = 0L
        accumulatedBreakSeconds = 0L
        sessionStartTime = System.currentTimeMillis()

        _isSessionActive.value = true
        _isTimerRunning.value = true
        _currentTab.value = 1 // Switch to active timer tab

        // Alert user that block 1 begins!
        alertManager.triggerAlert(AlertType.BLOCK_BEGINS, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)

        startTimerTicker()
    }

    fun togglePlayPause() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            resumeTimer()
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resumeTimer() {
        _isTimerRunning.value = true
        startTimerTicker()
    }

    fun addMinutes(minutes: Int) {
        val extra = minutes * 60
        _secondsRemaining.update { it + extra }
        _phaseTotalSeconds.update { it + extra }
    }

    fun toggleBlockCompletion(blockIndex: Int) {
        vibrateFeedback()
        _activeBlocks.update { list ->
            list.map { block ->
                if (block.index == blockIndex) {
                    val newState = !block.isCompleted
                    block.copy(
                        isCompleted = newState,
                        completedAt = if (newState) System.currentTimeMillis() else null
                    )
                } else {
                    block
                }
            }
        }
    }

    fun markCurrentBlockCompleted() {
        toggleBlockCompletion(_currentBlockIndex.value)
    }

    fun skipCurrentPhase() {
        onPhaseComplete()
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _isSessionActive.value) {
                delay(1000L)
                if (_secondsRemaining.value > 0) {
                    _secondsRemaining.update { it - 1 }
                    if (_currentPhase.value == TimerPhase.FOCUS) {
                        accumulatedFocusSeconds++
                    } else {
                        accumulatedBreakSeconds++
                    }
                } else {
                    onPhaseComplete()
                }
            }
        }
    }

    private fun onPhaseComplete() {
        val current = _currentPhase.value
        val currentIndex = _currentBlockIndex.value
        val totalBlocks = _activeBlocks.value.size

        if (current == TimerPhase.FOCUS) {
            // Automatically mark this block completed if not marked yet
            _activeBlocks.update { list ->
                list.map { b ->
                    if (b.index == currentIndex && !b.isCompleted) {
                        b.copy(isCompleted = true, completedAt = System.currentTimeMillis())
                    } else b
                }
            }

            if (currentIndex >= totalBlocks) {
                // Completed all blocks in session!
                alertManager.triggerAlert(AlertType.SESSION_COMPLETE, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)
                finishSessionPrompt()
            } else {
                // Block ended! Notify user and transition to break
                alertManager.triggerAlert(AlertType.BLOCK_ENDS, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)

                val isLongBreak = currentIndex % 4 == 0
                val breakMinutes = if (isLongBreak) configLongBreakMinutes.value else configShortBreakMinutes.value
                val breakSeconds = breakMinutes * 60

                _currentPhase.value = if (isLongBreak) TimerPhase.LONG_BREAK else TimerPhase.SHORT_BREAK
                _secondsRemaining.value = breakSeconds
                _phaseTotalSeconds.value = breakSeconds
            }
        } else {
            // Break completed, move to next study block!
            val nextIndex = currentIndex + 1
            if (nextIndex <= totalBlocks) {
                _currentBlockIndex.value = nextIndex
                val workSeconds = configWorkMinutes.value * 60
                _currentPhase.value = TimerPhase.FOCUS
                _secondsRemaining.value = workSeconds
                _phaseTotalSeconds.value = workSeconds

                // Alert user that next study block has begun!
                alertManager.triggerAlert(AlertType.BLOCK_BEGINS, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)
            } else {
                alertManager.triggerAlert(AlertType.SESSION_COMPLETE, _soundAlertsEnabled.value, _vibrationAlertsEnabled.value)
                finishSessionPrompt()
            }
        }
    }

    fun finishSessionPrompt() {
        pauseTimer()
        val totalPlanned = _activeBlocks.value.size
        val completedCount = _activeBlocks.value.count { it.isCompleted }
        val completionRate = if (totalPlanned > 0) completedCount.toFloat() / totalPlanned else 1f

        _lastSummary.value = SessionSummaryData(
            topic = configTopic.value.ifBlank { "Tema General" },
            subtopic = configSubtopic.value.ifBlank { "Sesión de Estudio" },
            totalBlocksPlanned = totalPlanned,
            blocksCompleted = completedCount,
            focusSeconds = accumulatedFocusSeconds,
            breakSeconds = accumulatedBreakSeconds,
            completionRate = completionRate,
            satisfactionRating = 5,
            notes = ""
        )
        _showSummaryDialog.value = true
    }

    fun saveCompletedSession(notes: String, rating: Int) {
        val user = _activeUser.value ?: return
        val summary = _lastSummary.value ?: return
        val blocks = _activeBlocks.value

        viewModelScope.launch {
            val session = StudySession(
                userId = user.id,
                topic = summary.topic,
                subtopic = summary.subtopic,
                totalBlocksPlanned = summary.totalBlocksPlanned,
                blocksCompleted = summary.blocksCompleted,
                workDurationMinutes = configWorkMinutes.value,
                shortBreakMinutes = configShortBreakMinutes.value,
                longBreakMinutes = configLongBreakMinutes.value,
                totalFocusSecondsSpent = summary.focusSeconds,
                totalBreakSecondsSpent = summary.breakSeconds,
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                isCompleted = summary.blocksCompleted >= summary.totalBlocksPlanned,
                notes = notes,
                satisfactionRating = rating
            )

            val blockRecords = blocks.map { b ->
                StudyBlockRecord(
                    sessionId = 0,
                    blockIndex = b.index,
                    title = b.title,
                    goal = b.goal,
                    durationMinutes = b.durationMinutes,
                    isCompleted = b.isCompleted,
                    completedAt = b.completedAt
                )
            }

            repository.saveCompletedSession(session, blockRecords)

            _isSessionActive.value = false
            _isTimerRunning.value = false
            _showSummaryDialog.value = false
            _currentTab.value = 2 // Go to stats tab to see the saved record!
        }
    }

    fun cancelSession() {
        pauseTimer()
        _isSessionActive.value = false
        _isTimerRunning.value = false
        _showSummaryDialog.value = false
        _currentTab.value = 0
    }

    fun dismissSummary() {
        _showSummaryDialog.value = false
        _isSessionActive.value = false
    }

    // User management
    fun switchActiveUser(user: UserProfile) {
        _activeUser.value = user
    }

    fun createUser(name: String, colorHex: String, dailyGoalMinutes: Int, emoji: String) {
        viewModelScope.launch {
            val newId = repository.createUser(name, colorHex, dailyGoalMinutes, emoji)
            _activeUser.value = UserProfile(
                id = newId,
                name = name,
                colorHex = colorHex,
                dailyGoalMinutes = dailyGoalMinutes,
                avatarEmoji = emoji
            )
        }
    }

    fun updateActiveUserDailyGoal(newGoalMinutes: Int) {
        val current = _activeUser.value ?: return
        val updated = current.copy(dailyGoalMinutes = newGoalMinutes)
        _activeUser.value = updated
        viewModelScope.launch {
            repository.updateUser(updated)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    private fun vibrateFeedback() {
        if (!_vibrationAlertsEnabled.value) return
        try {
            val context = getApplication<Application>().applicationContext
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                if (vibrator?.hasVibrator() == true) {
                    vibrator.vibrate(VibrationEffect.createOneShot(70L, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator?.hasVibrator() == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(70L, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(70L)
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration fails
        }
    }
}
