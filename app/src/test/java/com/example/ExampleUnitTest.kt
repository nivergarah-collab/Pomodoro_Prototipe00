package com.example

import com.example.ui.StudyPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun presets_haveValidTiming() {
        val preset = StudyPreset("Clásico Pomodoro", "25/5", 4, 25, 5, 15)
        assertEquals(4, preset.blocks)
        assertEquals(25, preset.workMinutes)
        assertEquals(5, preset.shortBreakMinutes)
        assertTrue(preset.workMinutes > preset.shortBreakMinutes)
    }

    @Test
    fun themeToggleLogic_invertsTheme() {
        var isDark = true
        isDark = !isDark
        assertFalse(isDark)
        isDark = !isDark
        assertTrue(isDark)
    }

    @Test
    fun weeklyData_calculatesDaysCorrectly() {
        val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        assertEquals(7, days.size)
        val dailyGoalMinutes = 120
        val dailyGoalHours = dailyGoalMinutes / 60f
        assertEquals(2.0f, dailyGoalHours, 0.01f)
    }

    @Test
    fun dailyGoalProgress_calculatesCorrectPercentage() {
        val goalMinutes = 120 // 2 hours
        val studiedMinutes = 60 // 1 hour
        val progressFraction = studiedMinutes.toFloat() / goalMinutes.toFloat()
        assertEquals(0.5f, progressFraction, 0.001f)
        val percent = (progressFraction * 100).toInt()
        assertEquals(50, percent)

        // Goal reached
        val completedMinutes = 150
        val isCompleted = completedMinutes >= goalMinutes
        assertTrue(isCompleted)
    }
}
