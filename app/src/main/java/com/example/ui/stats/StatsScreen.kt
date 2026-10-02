package com.example.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudySession
import com.example.ui.MainViewModel
import com.example.ui.components.DailyGoalProgressCard
import com.example.ui.components.DailyGoalSettingDialog
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.StatCard
import com.example.ui.components.UserSelectorBar
import com.example.ui.components.WeeklyStudyProductivityCard
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    onOpenUserDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val sessions by viewModel.userSessions.collectAsStateWithLifecycle()

    var sessionToDelete by remember { mutableStateOf<StudySession?>(null) }
    var showGoalEditor by remember { mutableStateOf(false) }

    val totalFocusSeconds = sessions.sumOf { it.totalFocusSecondsSpent }
    val totalFocusMinutes = totalFocusSeconds / 60
    val totalFocusHours = totalFocusMinutes / 60
    val remainingMinutes = totalFocusMinutes % 60
    val totalBlocksCompleted = sessions.sumOf { it.blocksCompleted }
    val averageRating = if (sessions.isNotEmpty()) {
        String.format(Locale.getDefault(), "%.1f", sessions.map { it.satisfactionRating }.average())
    } else "0.0"

    // Prepare chart data for last 7 sessions
    val chartSessions = sessions.take(7).reversed()
    val chartValues = chartSessions.map { (it.totalFocusSecondsSpent / 60).toFloat() }
    val chartLabels = chartSessions.map {
        val topic = it.topic.trim()
        if (topic.length > 5) topic.take(4) + ".." else topic
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("stats_screen")
    ) {
        item {
            // User header & selector
            UserSelectorBar(
                user = activeUser,
                onSwitchUserClick = onOpenUserDialog
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Goal Dynamic Progress Card
            DailyGoalProgressCard(
                user = activeUser,
                sessions = sessions,
                onEditGoalClick = { showGoalEditor = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Tiempo Total",
                    value = if (totalFocusHours > 0) "${totalFocusHours}h ${remainingMinutes}m" else "${totalFocusMinutes}m",
                    subtitle = "Enfoque acumulado",
                    accentColor = PomoPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sesiones",
                    value = "${sessions.size}",
                    subtitle = "Registradas",
                    accentColor = BreakGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Bloques Hechos",
                    value = "$totalBlocksCompleted",
                    subtitle = "Pomodoros",
                    accentColor = PomoSecondary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Calificación",
                    value = "★ $averageRating",
                    subtitle = "Promedio enfoque",
                    accentColor = PomoAccentGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Weekly Study Productivity Chart (Weekly hours summary, daily breakdown & target goal)
            WeeklyStudyProductivityCard(
                sessions = sessions,
                dailyGoalMinutes = activeUser?.dailyGoalMinutes ?: 100
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Individual Sessions Bar Chart (Collapsible/Secondary visualization)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Actividad por Sesión Individual (min)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SimpleBarChart(
                        values = chartValues,
                        labels = chartLabels,
                        primaryColor = PomoPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // History Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Sesiones (${sessions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (sessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aún no tienes sesiones registradas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Completa tu primera sesión Pomodoro para ver tu progreso, tiempo y notas aquí.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Button(
                            onClick = { viewModel.setTab(0) },
                            colors = ButtonDefaults.buttonColors(containerColor = PomoPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Iniciar mi primera sesión")
                        }
                    }
                }
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                SessionHistoryCard(
                    session = session,
                    onDeleteClick = { sessionToDelete = session },
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showGoalEditor) {
        DailyGoalSettingDialog(
            currentGoalMinutes = activeUser?.dailyGoalMinutes ?: 120,
            onSaveGoal = { newGoal ->
                viewModel.updateActiveUserDailyGoal(newGoal)
            },
            onDismiss = { showGoalEditor = false }
        )
    }

    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Eliminar sesión") },
            text = { Text("¿Deseas eliminar el registro de esta sesión de estudio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionToDelete?.let { viewModel.deleteSession(it.id) }
                        sessionToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SessionHistoryCard(
    session: StudySession,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(session.endTime))
    val focusMinutes = session.totalFocusSecondsSpent / 60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("session_history_item_${session.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.topic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = session.subtopic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar sesión",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PomoPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${focusMinutes}m enfoque",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PomoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BreakGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${session.blocksCompleted}/${session.totalBlocksPlanned} bloques",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BreakGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PomoAccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${session.satisfactionRating}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 \"${session.notes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
