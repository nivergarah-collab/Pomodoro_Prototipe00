package com.example.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoSecondary

val AvatarEmojiList = listOf("🎓", "🚀", "💡", "🧠", "📚", "⭐", "🔥", "🎯", "🏆", "☕")
val ProfileColorList = listOf("#E04F36", "#E28743", "#F59E0B", "#10B981", "#0284C7", "#8B5CF6")

@Composable
fun UserManagementDialog(
    users: List<UserProfile>,
    activeUser: UserProfile?,
    onSelectUser: (UserProfile) -> Unit,
    onCreateUser: (name: String, colorHex: String, dailyGoalMinutes: Int, emoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(ProfileColorList.first()) }
    var selectedEmoji by remember { mutableStateOf(AvatarEmojiList.first()) }
    var dailyGoalMinutes by remember { mutableIntStateOf(100) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("user_management_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCreatingNew) "Nuevo Perfil de Estudio" else "Perfiles de Usuario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isCreatingNew) {
                    Text(
                        text = "Selecciona el perfil para registrar tus sesiones y estadísticas:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    users.forEach { user ->
                        val isSelected = user.id == activeUser?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectUser(user)
                                    onDismiss()
                                }
                                .testTag("user_item_${user.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                }
                            ),
                            border = if (isSelected) {
                                androidx.compose.foundation.BorderStroke(2.dp, PomoPrimary)
                            } else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                try {
                                                    Color(android.graphics.Color.parseColor(user.colorHex))
                                                } catch (_: Exception) {
                                                    PomoPrimary
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = user.avatarEmoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Meta diaria: ${user.dailyGoalMinutes} min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Activo",
                                        tint = PomoPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isCreatingNew = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_user_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Nuevo Perfil")
                    }
                } else {
                    // Create User Form
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("Nombre del estudiante o perfil") },
                        placeholder = { Text("Ej. María, Juan, Posgrado...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_user_name_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Elige tu avatar emoji:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvatarEmojiList.take(5).forEach { emoji ->
                            EmojiChip(
                                emoji = emoji,
                                isSelected = selectedEmoji == emoji,
                                onSelect = { selectedEmoji = emoji }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvatarEmojiList.drop(5).forEach { emoji ->
                            EmojiChip(
                                emoji = emoji,
                                isSelected = selectedEmoji == emoji,
                                onSelect = { selectedEmoji = emoji }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Color distintivo:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileColorList.forEach { hex ->
                            val c = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (_: Exception) {
                                PomoPrimary
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable { selectedColor = hex }
                                    .border(
                                        width = if (selectedColor == hex) 3.dp else 0.dp,
                                        color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Meta diaria de estudio: ${String.format(java.util.Locale.getDefault(), "%.1f", dailyGoalMinutes / 60f)} horas ($dailyGoalMinutes min)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    androidx.compose.material3.Slider(
                        value = dailyGoalMinutes.toFloat(),
                        onValueChange = { dailyGoalMinutes = it.toInt() },
                        valueRange = 30f..300f,
                        steps = 8
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isCreatingNew = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                if (newUserName.isNotBlank()) {
                                    onCreateUser(newUserName, selectedColor, dailyGoalMinutes, selectedEmoji)
                                    onDismiss()
                                }
                            },
                            enabled = newUserName.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_create_user_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PomoPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiChip(
    emoji: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PomoPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 22.sp)
    }
}
