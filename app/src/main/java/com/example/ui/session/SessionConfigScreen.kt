package com.example.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.StudyPreset
import com.example.ui.components.DailyGoalSettingDialog
import com.example.ui.components.UserSelectorBar
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoSecondary
import com.example.util.AlertType
import java.util.Locale

val QuickTopicSuggestions = listOf(
    "Programación", "Matemáticas", "Idiomas", "Medicina", "Derecho", "Historia", "Física", "Diseño"
)

@Composable
fun SessionConfigScreen(
    viewModel: MainViewModel,
    onOpenUserDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val topic by viewModel.configTopic.collectAsStateWithLifecycle()
    val subtopic by viewModel.configSubtopic.collectAsStateWithLifecycle()
    val blocksCount by viewModel.configBlocksCount.collectAsStateWithLifecycle()
    val workMinutes by viewModel.configWorkMinutes.collectAsStateWithLifecycle()
    val shortBreakMinutes by viewModel.configShortBreakMinutes.collectAsStateWithLifecycle()
    val longBreakMinutes by viewModel.configLongBreakMinutes.collectAsStateWithLifecycle()
    val blockGoals by viewModel.configBlockGoals.collectAsStateWithLifecycle()
    val soundAlertsEnabled by viewModel.soundAlertsEnabled.collectAsStateWithLifecycle()
    val vibrationAlertsEnabled by viewModel.vibrationAlertsEnabled.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val isDarkThemePref by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isEffectiveDark = isDarkThemePref ?: isSystemDark

    var showBlockGoalsCustomizer by remember { mutableStateOf(false) }
    var showGoalEditor by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        // User Selector at top
        UserSelectorBar(
            user = activeUser,
            onSwitchUserClick = onOpenUserDialog
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Compact Today's Goal Quick Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { showGoalEditor = true }
                .testTag("config_daily_goal_card")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = PomoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Meta de estudio para hoy:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%.1f h (%d min)",
                            (activeUser?.dailyGoalMinutes ?: 120) / 60f,
                            activeUser?.dailyGoalMinutes ?: 120
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PomoPrimary
                    )
                }

                Text(
                    text = "Ajustar",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero banner card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_study_banner),
                    contentDescription = "Espacio de estudio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC0F172A))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Configura tu Sesión Pomodoro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Bloques enfocados y descansos inteligentes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Presets selector
        Text(
            text = "Plantillas Rápidas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            viewModel.presets.forEach { preset ->
                val isSelected = blocksCount == preset.blocks &&
                        workMinutes == preset.workMinutes &&
                        shortBreakMinutes == preset.shortBreakMinutes

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(2.dp, PomoPrimary)
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    },
                    modifier = Modifier
                        .clickable { viewModel.applyPreset(preset) }
                        .testTag("preset_${preset.name.replace(" ", "_")}")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PomoPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${preset.blocks} bloques • ${preset.workMinutes}m/${preset.shortBreakMinutes}m",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Topic & Subtopic Section
        Text(
            text = "Materia y Tema de Estudio",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { viewModel.configTopic.value = it },
            label = { Text("Tema o Asignatura") },
            placeholder = { Text("Ej. Álgebra Lineal, Medicina Interna, Inglés B2...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("topic_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick topic suggestion chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickTopicSuggestions.forEach { suggestion ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { viewModel.configTopic.value = suggestion }
                ) {
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = subtopic,
            onValueChange = { viewModel.configSubtopic.value = it },
            label = { Text("Subtema u Objetivo principal") },
            placeholder = { Text("Ej. Resolver guía de ejercicios, Memorizar vocabulario...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("subtopic_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders & Durations
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Blocks count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Número de bloques de estudio",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$blocksCount ${if (blocksCount == 1) "bloque" else "bloques"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PomoPrimary
                    )
                }
                Slider(
                    value = blocksCount.toFloat(),
                    onValueChange = { viewModel.configBlocksCount.value = it.toInt() },
                    valueRange = 1f..8f,
                    steps = 6,
                    modifier = Modifier.testTag("blocks_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Work duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duración de cada bloque (Enfoque)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$workMinutes min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PomoPrimary
                    )
                }
                Slider(
                    value = workMinutes.toFloat(),
                    onValueChange = { viewModel.configWorkMinutes.value = it.toInt() },
                    valueRange = 10f..60f,
                    steps = 9,
                    modifier = Modifier.testTag("work_duration_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Short break
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Descanso corto",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$shortBreakMinutes min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BreakGreen
                    )
                }
                Slider(
                    value = shortBreakMinutes.toFloat(),
                    onValueChange = { viewModel.configShortBreakMinutes.value = it.toInt() },
                    valueRange = 2f..15f,
                    steps = 12
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Long break
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Descanso largo (cada 4 bloques)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$longBreakMinutes min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                }
                Slider(
                    value = longBreakMinutes.toFloat(),
                    onValueChange = { viewModel.configLongBreakMinutes.value = it.toInt() },
                    valueRange = 10f..30f,
                    steps = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Switcher Card for Dark/Light Mode
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_switch_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                                if (isEffectiveDark) Color(0xFF6366F1).copy(alpha = 0.22f)
                                else PomoAccentGold.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEffectiveDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isEffectiveDark) Color(0xFFA5B4FC) else PomoAccentGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isEffectiveDark) "Tema Oscuro (Modo Nocturno)" else "Tema Claro",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isEffectiveDark) "Confort visual en entornos con poca luz" else "Ideal para estudiar con luz ambiental alta",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isEffectiveDark,
                    onCheckedChange = { viewModel.setDarkTheme(it) },
                    thumbContent = {
                        Icon(
                            imageVector = if (isEffectiveDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFA5B4FC),
                        checkedTrackColor = Color(0xFF4338CA),
                        uncheckedThumbColor = PomoAccentGold,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("dark_mode_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notification Sounds & Vibrations Alert Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("alerts_settings_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PomoAccentGold.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = PomoAccentGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Alertas de Sonido y Vibración",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avisa cuando inicia o termina cada bloque",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sound Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (soundAlertsEnabled) Icons.Default.VolumeUp else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (soundAlertsEnabled) PomoPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sonidos de notificación",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Campanas al iniciar y terminar el tiempo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = soundAlertsEnabled,
                        onCheckedChange = { viewModel.setSoundAlerts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PomoPrimary
                        ),
                        modifier = Modifier.testTag("sound_alerts_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = if (vibrationAlertsEnabled) PomoPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Vibración háptica",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Patrones de pulso distintivos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = vibrationAlertsEnabled,
                        onCheckedChange = { viewModel.setVibrationAlerts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PomoPrimary
                        ),
                        modifier = Modifier.testTag("vibration_alerts_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick alert preview test buttons
                Text(
                    text = "Probar alertas en este dispositivo:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.testAlert(AlertType.BLOCK_BEGINS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_sound_start_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🔔 Inicio de bloque", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.testAlert(AlertType.BLOCK_ENDS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_sound_end_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⏰ Fin de bloque", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Customizable block objectives expander
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBlockGoalsCustomizer = !showBlockGoalsCustomizer }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Personalizar objetivo por bloque",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Opcional: detalla qué harás en cada bloque",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (showBlockGoalsCustomizer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = showBlockGoalsCustomizer) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                (1..blocksCount).forEach { index ->
                    val currentVal = blockGoals[index] ?: ""
                    OutlinedTextField(
                        value = currentVal,
                        onValueChange = { viewModel.setBlockGoal(index, it) },
                        label = { Text("Objetivo del Bloque $index") },
                        placeholder = { Text("Ej. Bloque $index: Resolver problemas 1 a 5") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start session button
        Button(
            onClick = { viewModel.startSession() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_session_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PomoPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Sesión de Estudio (${blocksCount * workMinutes} min)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
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
}
