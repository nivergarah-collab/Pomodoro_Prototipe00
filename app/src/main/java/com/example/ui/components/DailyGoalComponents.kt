package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudySession
import com.example.data.model.UserProfile
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoSecondary
import java.util.Calendar
import java.util.Locale

@Composable
fun DailyGoalProgressCard(
    user: UserProfile?,
    sessions: List<StudySession>,
    onEditGoalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goalMinutes = (user?.dailyGoalMinutes ?: 120).coerceAtLeast(15)
    val goalHours = goalMinutes / 60f

    // Calculate today's exact focus seconds from completed sessions
    val todayFocusSeconds = remember(sessions) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfToday = calendar.timeInMillis

        sessions.filter { it.endTime in startOfToday..endOfToday }
            .sumOf { it.totalFocusSecondsSpent }
    }

    val todayFocusMinutes = (todayFocusSeconds / 60).toInt()
    val todayFocusHours = todayFocusMinutes / 60f
    val remainingMinutes = (goalMinutes - todayFocusMinutes).coerceAtLeast(0)
    val remainingHours = remainingMinutes / 60f

    val progressFraction = (todayFocusMinutes.toFloat() / goalMinutes.toFloat()).coerceAtLeast(0f)
    val progressPercent = (progressFraction * 100).toInt()
    val isGoalCompleted = todayFocusMinutes >= goalMinutes

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "dailyGoalProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_goal_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGoalCompleted) BreakGreen.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Goal Title & Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isGoalCompleted) BreakGreen.copy(alpha = 0.18f)
                                else PomoPrimary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGoalCompleted) Icons.Default.EmojiEvents else Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = if (isGoalCompleted) BreakGreen else PomoPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Meta Diaria de Estudio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isGoalCompleted) "¡Objetivo alcanzado hoy!" else "Progreso del día en tiempo real",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGoalCompleted) BreakGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Edit Goal Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onEditGoalClick() }
                        .testTag("edit_daily_goal_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Meta",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ajustar",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Metrics: Hours completed vs Goal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", todayFocusHours),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isGoalCompleted) BreakGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " / ${String.format(Locale.getDefault(), "%.1f", goalHours)} h",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    Text(
                        text = "${todayFocusMinutes} de ${goalMinutes} minutos completados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isGoalCompleted) BreakGreen.copy(alpha = 0.18f) else PomoPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isGoalCompleted) BreakGreen else PomoPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            if (isGoalCompleted) {
                                Brush.horizontalGradient(
                                    listOf(BreakGreen, Color(0xFF059669), PomoAccentGold)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(PomoPrimary, PomoSecondary)
                                )
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Motivational Status Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isGoalCompleted -> "🎉 ¡Felicidades! Superaste tu meta diaria."
                        todayFocusMinutes == 0 -> "Completa sesiones Pomodoro para avanzar."
                        progressFraction >= 0.75f -> "¡Casi listo! Solo faltan ${remainingMinutes}m."
                        progressFraction >= 0.5f -> "¡A mitad de camino! Excelente ritmo."
                        else -> "Buen comienzo hoy. Faltan ${String.format(Locale.getDefault(), "%.1f", remainingHours)}h."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isGoalCompleted) BreakGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isGoalCompleted) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun DailyGoalSettingDialog(
    currentGoalMinutes: Int,
    onSaveGoal: (newGoalMinutes: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(currentGoalMinutes.coerceIn(30, 480)) }
    val selectedHours = selectedMinutes / 60f

    val presets = listOf(
        Pair("1.0 h", 60),
        Pair("1.5 h", 90),
        Pair("2.0 h", 120),
        Pair("3.0 h", 180),
        Pair("4.0 h", 240),
        Pair("5.0 h", 300)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("daily_goal_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PomoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                tint = PomoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Establecer Meta Diaria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Hour Display Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f Horas", selectedHours),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PomoPrimary
                        )
                        Text(
                            text = "($selectedMinutes minutos de estudio de enfoque al día)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick preset chips
                Text(
                    text = "Selección Rápida de Metas:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.take(3).forEach { (label, mins) ->
                        PresetChip(
                            label = label,
                            isSelected = selectedMinutes == mins,
                            onClick = { selectedMinutes = mins },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.drop(3).forEach { (label, mins) ->
                        PresetChip(
                            label = label,
                            isSelected = selectedMinutes == mins,
                            onClick = { selectedMinutes = mins },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ajuste preciso:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${selectedMinutes}m",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PomoPrimary
                    )
                }

                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = (it / 15).toInt() * 15 },
                    valueRange = 30f..480f,
                    steps = 29, // 15 min steps from 30 to 480 min (0.5h to 8h)
                    colors = SliderDefaults.colors(
                        thumbColor = PomoPrimary,
                        activeTrackColor = PomoPrimary
                    ),
                    modifier = Modifier.testTag("goal_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description of intensity
                Text(
                    text = when {
                        selectedMinutes <= 90 -> "💡 Meta ligera: Ideal para constancia y sesiones cortas sin agotamiento."
                        selectedMinutes <= 180 -> "⚡ Meta equilibrada: Excelente ritmo diario para estudiantes o profesionales."
                        selectedMinutes <= 300 -> "🔥 Meta intensiva: Alta productividad para días de exámenes o proyectos."
                        else -> "🏆 Meta Maratón: Máximo rendimiento de estudio profundo."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onSaveGoal(selectedMinutes)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_daily_goal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PomoPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Guardar Meta")
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PomoPrimary) else null,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PomoPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
