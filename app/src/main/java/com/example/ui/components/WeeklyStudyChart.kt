package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySession
import com.example.ui.theme.BreakGreen
import com.example.ui.theme.PomoAccentGold
import com.example.ui.theme.PomoPrimary
import com.example.ui.theme.PomoPrimaryDark
import com.example.ui.theme.PomoSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStudyData(
    val dayNameShort: String, // "Lun", "Mar", ...
    val dayNameFull: String,  // "Lunes", "Martes", ...
    val dateString: String,   // "28 Sep"
    val hours: Float,
    val minutes: Int,
    val sessionsCount: Int,
    val topics: List<String>,
    val isToday: Boolean,
    val meetsGoal: Boolean
)

data class WeeklyStudySummary(
    val weekLabel: String, // "Semana del 29 Sep - 05 Oct"
    val days: List<DayStudyData>,
    val totalHours: Float,
    val totalMinutes: Int,
    val averageHoursPerDay: Float,
    val bestDay: DayStudyData?,
    val goalHoursWeekly: Float,
    val goalCompletionPercentage: Int,
    val productivityLevel: String
)

@Composable
fun WeeklyStudyProductivityCard(
    sessions: List<StudySession>,
    dailyGoalMinutes: Int,
    modifier: Modifier = Modifier
) {
    var weekOffset by remember { mutableIntStateOf(0) } // 0 = current week, -1 = last week, etc.
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    val weeklySummary = remember(sessions, dailyGoalMinutes, weekOffset) {
        computeWeeklySummary(sessions, dailyGoalMinutes, weekOffset)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_productivity_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Title & Week navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PomoPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PomoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Resumen Semanal de Estudio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weeklySummary.weekLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Week selector buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            weekOffset--
                            selectedDayIndex = -1
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("prev_week_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Semana anterior",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (weekOffset == 0) "Actual" else "${weekOffset}s",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (weekOffset < 0) {
                                weekOffset++
                                selectedDayIndex = -1
                            }
                        },
                        enabled = weekOffset < 0,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("next_week_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Semana siguiente",
                            tint = if (weekOffset < 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main KPI Metrics Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Horas Totales",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f h", weeklySummary.totalHours),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PomoPrimary
                    )
                    Text(
                        text = "${weeklySummary.totalMinutes} min de enfoque",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Promedio Diario",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f h", weeklySummary.averageHoursPerDay),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Meta: ${(dailyGoalMinutes / 60f)}h/día",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Productividad",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (weeklySummary.goalCompletionPercentage >= 70) BreakGreen.copy(alpha = 0.15f) else PomoAccentGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${weeklySummary.goalCompletionPercentage}% meta",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (weeklySummary.goalCompletionPercentage >= 70) BreakGreen else PomoAccentGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = weeklySummary.productivityLevel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Weekly Bar Chart
            Text(
                text = "Distribución por Día de la Semana (toca una barra para ver detalles):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeeklyBarChartInteractive(
                days = weeklySummary.days,
                dailyGoalHours = dailyGoalMinutes / 60f,
                selectedIndex = selectedDayIndex,
                onDaySelected = { selectedDayIndex = if (selectedDayIndex == it) -1 else it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Day Detail or Best Day Highlight
            if (selectedDayIndex in weeklySummary.days.indices) {
                val selectedDay = weeklySummary.days[selectedDayIndex]
                DayDetailPanel(day = selectedDay, dailyGoalHours = dailyGoalMinutes / 60f)
            } else if (weeklySummary.bestDay != null && weeklySummary.bestDay.hours > 0f) {
                // Best day highlight chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = PomoAccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Día más productivo: ${weeklySummary.bestDay.dayNameFull} (${String.format(Locale.getDefault(), "%.1f h", weeklySummary.bestDay.hours)})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyBarChartInteractive(
    days: List<DayStudyData>,
    dailyGoalHours: Float,
    selectedIndex: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxHours = (days.maxOfOrNull { it.hours } ?: 1f).coerceAtLeast(dailyGoalHours).coerceAtLeast(2.5f)

    Box(modifier = modifier) {
        // Goal line & Bars canvas
        Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 28.dp, top = 20.dp, start = 12.dp, end = 12.dp)) {
            val chartWidth = size.width
            val chartHeight = size.height
            val goalY = chartHeight * (1f - (dailyGoalHours / maxHours).coerceIn(0f, 1f))

            // Draw Target Goal Dotted Line
            drawLine(
                color = Color(0xFFF59E0B).copy(alpha = 0.5f),
                start = Offset(0f, goalY),
                end = Offset(chartWidth, goalY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Days columns
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = selectedIndex == index
                val fraction = (day.hours / maxHours).coerceIn(0f, 1f)

                val barHeightFraction by animateFloatAsState(
                    targetValue = fraction,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "barHeight_$index"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDaySelected(index) }
                        .padding(horizontal = 2.dp)
                        .testTag("day_bar_${day.dayNameShort}")
                ) {
                    // Hours label above bar
                    Text(
                        text = if (day.hours > 0f) String.format(Locale.getDefault(), "%.1fh", day.hours) else "",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PomoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // The Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight(0.72f * barHeightFraction.coerceAtLeast(0.04f))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                when {
                                    isSelected -> Brush.verticalGradient(listOf(PomoSecondary, PomoPrimary))
                                    day.meetsGoal -> Brush.verticalGradient(listOf(BreakGreen, Color(0xFF059669)))
                                    day.hours > 0f -> Brush.verticalGradient(listOf(PomoPrimaryDark, PomoPrimary))
                                    else -> Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Day Name Label
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            isSelected -> PomoPrimary
                            day.isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> Color.Transparent
                        }
                    ) {
                        Text(
                            text = day.dayNameShort,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> Color.White
                                day.isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetailPanel(
    day: DayStudyData,
    dailyGoalHours: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("day_detail_panel"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${day.dayNameFull}, ${day.dateString}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PomoPrimary
                        ) {
                            Text(
                                text = "HOY",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${day.hours} h (${day.minutes} min)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (day.meetsGoal) BreakGreen else PomoPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${day.sessionsCount} sesiones completadas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (day.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Temas estudiados: ${day.topics.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun computeWeeklySummary(
    sessions: List<StudySession>,
    dailyGoalMinutes: Int,
    weekOffset: Int
): WeeklyStudySummary {
    val calendar = Calendar.getInstance()
    // Configure to first day of week = Monday
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    // Set to Monday of this target week at 00:00:00
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfWeekMillis = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_YEAR, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfWeekMillis = calendar.timeInMillis

    // Format week range label
    val weekDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val weekLabel = "Semana del ${weekDateFormat.format(Date(startOfWeekMillis))} - ${weekDateFormat.format(Date(endOfWeekMillis))}"

    // Filter sessions belonging to this week
    val weekSessions = sessions.filter { it.endTime in startOfWeekMillis..endOfWeekMillis }

    val dayNamesShort = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val dayNamesFull = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val calendarDays = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
        Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    )

    val todayCalendar = Calendar.getInstance()
    val todayDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR)
    val todayYear = todayCalendar.get(Calendar.YEAR)

    val dailyGoalHours = dailyGoalMinutes / 60f

    val daysData = (0..6).map { i ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = startOfWeekMillis
        cal.add(Calendar.DAY_OF_YEAR, i)

        val dayStart = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val dayEnd = cal.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        val daySessions = weekSessions.filter { it.endTime in dayStart..dayEnd }
        val dayFocusSeconds = daySessions.sumOf { it.totalFocusSecondsSpent }
        val dayMinutes = (dayFocusSeconds / 60).toInt()
        val dayHours = dayMinutes / 60f
        val topics = daySessions.map { it.topic.trim() }.distinct().filter { it.isNotBlank() }

        val isToday = cal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear && cal.get(Calendar.YEAR) == todayYear

        DayStudyData(
            dayNameShort = dayNamesShort[i],
            dayNameFull = dayNamesFull[i],
            dateString = weekDateFormat.format(Date(dayStart)),
            hours = dayHours,
            minutes = dayMinutes,
            sessionsCount = daySessions.size,
            topics = topics,
            isToday = isToday,
            meetsGoal = dayHours >= dailyGoalHours && dayHours > 0f
        )
    }

    val totalHours = daysData.sumOf { it.hours.toDouble() }.toFloat()
    val totalMinutes = daysData.sumOf { it.minutes }
    val averageHoursPerDay = totalHours / 7f
    val bestDay = daysData.maxByOrNull { it.hours }
    val weeklyGoalHours = (dailyGoalMinutes * 7) / 60f
    val goalCompletionPercentage = if (weeklyGoalHours > 0f) {
        ((totalHours / weeklyGoalHours) * 100).toInt().coerceIn(0, 100)
    } else 0

    val productivityLevel = when {
        totalHours >= weeklyGoalHours -> "Excelente meta superada 🚀"
        totalHours >= weeklyGoalHours * 0.7f -> "Muy productivo 🔥"
        totalHours >= weeklyGoalHours * 0.4f -> "Buen ritmo constante ⚡"
        totalHours > 0f -> "En progreso 🌱"
        else -> "Sin registros esta semana"
    }

    return WeeklyStudySummary(
        weekLabel = weekLabel,
        days = daysData,
        totalHours = totalHours,
        totalMinutes = totalMinutes,
        averageHoursPerDay = averageHoursPerDay,
        bestDay = bestDay,
        goalHoursWeekly = weeklyGoalHours,
        goalCompletionPercentage = goalCompletionPercentage,
        productivityLevel = productivityLevel
    )
}
