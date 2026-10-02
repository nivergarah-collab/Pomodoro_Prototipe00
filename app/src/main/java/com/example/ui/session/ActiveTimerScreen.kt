package com.example.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.MainViewModel
import com.example.ui.TimerPhase
import com.example.ui.components.BlockTimelineCard
import com.example.ui.components.PomodoroTimerArc
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoSecondary

@Composable
fun ActiveTimerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentPhase.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.secondsRemaining.collectAsStateWithLifecycle()
    val phaseTotalSeconds by viewModel.phaseTotalSeconds.collectAsStateWithLifecycle()
    val currentBlockIndex by viewModel.currentBlockIndex.collectAsStateWithLifecycle()
    val activeBlocks by viewModel.activeBlocks.collectAsStateWithLifecycle()
    val topic by viewModel.configTopic.collectAsStateWithLifecycle()
    val subtopic by viewModel.configSubtopic.collectAsStateWithLifecycle()
    val soundAlertsEnabled by viewModel.soundAlertsEnabled.collectAsStateWithLifecycle()
    val vibrationAlertsEnabled by viewModel.vibrationAlertsEnabled.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val isDarkThemePref by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isEffectiveDark = isDarkThemePref ?: isSystemDark

    var showCancelDialog by remember { mutableStateOf(false) }

    if (!isSessionActive) {
        // Idle screen when no session is running
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No hay sesión en curso",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ve a la pestaña 'Configurar' para preparar tus bloques de estudio y comenzar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.setTab(0) },
                    colors = ButtonDefaults.buttonColors(containerColor = PomoPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Configurar Nueva Sesión")
                }
            }
        }
        return
    }

    val currentBlock = activeBlocks.find { it.index == currentBlockIndex }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header with Session Topic
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = topic.ifBlank { "Tema General" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subtopic.ifBlank { "Sesión de Estudio" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Audio & Vibration Alert Toggles in Active Session
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (soundAlertsEnabled) PomoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (soundAlertsEnabled) PomoPrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleSoundAlerts() }
                                .testTag("quick_toggle_sound")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (soundAlertsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = null,
                                    tint = if (soundAlertsEnabled) PomoPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (soundAlertsEnabled) "Sonido ON" else "Silenciado",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (soundAlertsEnabled) PomoPrimary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (vibrationAlertsEnabled) BreakGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (vibrationAlertsEnabled) BreakGreen else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleVibrationAlerts() }
                                .testTag("quick_toggle_vibration")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = null,
                                    tint = if (vibrationAlertsEnabled) BreakGreen else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (vibrationAlertsEnabled) "Vibración" else "Sin vibrar",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (vibrationAlertsEnabled) BreakGreen else MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        // Theme Mode Quick Toggle Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isEffectiveDark) Color(0xFF6366F1).copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isEffectiveDark) Color(0xFFA5B4FC) else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleDarkTheme(isEffectiveDark) }
                                .testTag("quick_toggle_theme")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isEffectiveDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (isEffectiveDark) Color(0xFFA5B4FC) else PomoAccentGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEffectiveDark) "Oscuro" else "Claro",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isEffectiveDark) Color(0xFFA5B4FC) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timer display arc
            PomodoroTimerArc(
                phase = currentPhase,
                secondsRemaining = secondsRemaining,
                totalSeconds = phaseTotalSeconds,
                isRunning = isTimerRunning,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Timer Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // +5 min button
                FilledTonalButton(
                    onClick = { viewModel.addMinutes(5) },
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text("+5m", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Play / Pause main button
                Button(
                    onClick = { viewModel.togglePlayPause() },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("play_pause_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentPhase == TimerPhase.FOCUS) PomoPrimary else BreakGreen
                    )
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pausar" else "Reanudar",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Skip phase button
                FilledTonalButton(
                    onClick = { viewModel.skipCurrentPhase() },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("skip_phase_button"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Siguiente fase", modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current block quick action banner
            if (currentBlock != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("current_block_banner"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentBlock.isCompleted) {
                            BreakGreen.copy(alpha = 0.15f)
                        } else {
                            PomoPrimary.copy(alpha = 0.12f)
                        }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (currentBlock.isCompleted) BreakGreen else PomoPrimary
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Bloque ${currentBlock.index} de ${activeBlocks.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentBlock.isCompleted) BreakGreen else PomoPrimary
                                )
                                Text(
                                    text = currentBlock.goal.ifBlank { "Estudio y concentración profunda" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button to mark current block completed immediately!
                        Button(
                            onClick = { viewModel.markCurrentBlockCompleted() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("mark_block_completed_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentBlock.isCompleted) BreakGreen else PomoPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (currentBlock.isCompleted) Icons.Default.Check else Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentBlock.isCompleted) "¡Bloque marcado como Completado!" else "Marcar bloque como completado",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action row: End session or cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = { viewModel.finishSessionPrompt() },
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("finish_session_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PomoAccentGold),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finalizar y Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Blocks Timeline header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso de Bloques",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${activeBlocks.count { it.isCompleted }}/${activeBlocks.size} hechos",
                    style = MaterialTheme.typography.labelLarge,
                    color = BreakGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // List of all session blocks
        items(activeBlocks, key = { it.index }) { block ->
            BlockTimelineCard(
                index = block.index,
                title = block.title,
                goal = block.goal,
                durationMinutes = block.durationMinutes,
                isCurrent = block.index == currentBlockIndex,
                isCompleted = block.isCompleted,
                onToggleCompleted = { viewModel.toggleBlockCompletion(block.index) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("¿Cancelar sesión actual?") },
            text = { Text("Si cancelas ahora, no se guardarán las estadísticas de esta sesión en tu registro.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelSession()
                    }
                ) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Continuar estudiando")
                }
            }
        )
    }
}
