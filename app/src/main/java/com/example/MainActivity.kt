package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.session.ActiveTimerScreen
import com.example.ui.session.SessionConfigScreen
import com.example.ui.session.SessionSummaryDialog
import com.example.ui.stats.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PomoPrimary
import com.example.ui.user.UserManagementDialog

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDarkThemePref by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isDarkTheme = isDarkThemePref ?: systemDark

            MyApplicationTheme(darkTheme = isDarkTheme) {
                PomoStudyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PomoStudyApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()
    val showSummaryDialog by viewModel.showSummaryDialog.collectAsStateWithLifecycle()
    val lastSummary by viewModel.lastSummary.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()

    var showUserDialog by remember { mutableStateOf(false) }

    // Support BackHandler for tabs navigation
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Filled.Tune else Icons.Outlined.Tune,
                            contentDescription = "Configurar"
                        )
                    },
                    label = { Text("Configurar") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = PomoPrimary.copy(alpha = 0.15f),
                        selectedIconColor = PomoPrimary,
                        selectedTextColor = PomoPrimary
                    ),
                    modifier = Modifier.testTag("tab_config")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (isSessionActive) {
                                    Badge(
                                        containerColor = PomoPrimary,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                                contentDescription = "En Curso"
                            )
                        }
                    },
                    label = { Text("Temporizador") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = PomoPrimary.copy(alpha = 0.15f),
                        selectedIconColor = PomoPrimary,
                        selectedTextColor = PomoPrimary
                    ),
                    modifier = Modifier.testTag("tab_timer")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Filled.Assessment else Icons.Outlined.Assessment,
                            contentDescription = "Estadísticas"
                        )
                    },
                    label = { Text("Estadísticas") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = PomoPrimary.copy(alpha = 0.15f),
                        selectedIconColor = PomoPrimary,
                        selectedTextColor = PomoPrimary
                    ),
                    modifier = Modifier.testTag("tab_stats")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentTab) {
                0 -> SessionConfigScreen(
                    viewModel = viewModel,
                    onOpenUserDialog = { showUserDialog = true }
                )
                1 -> ActiveTimerScreen(
                    viewModel = viewModel
                )
                2 -> StatsScreen(
                    viewModel = viewModel,
                    onOpenUserDialog = { showUserDialog = true }
                )
            }
        }
    }

    // User management modal dialog
    if (showUserDialog) {
        UserManagementDialog(
            users = allUsers,
            activeUser = activeUser,
            onSelectUser = { viewModel.switchActiveUser(it) },
            onCreateUser = { name, colorHex, dailyGoalMinutes, emoji ->
                viewModel.createUser(name, colorHex, dailyGoalMinutes, emoji)
            },
            onDismiss = { showUserDialog = false }
        )
    }

    // Session completion and summary dialog
    if (showSummaryDialog && lastSummary != null) {
        SessionSummaryDialog(
            summary = lastSummary!!,
            onSaveSession = { notes, rating ->
                viewModel.saveCompletedSession(notes, rating)
            },
            onDismiss = { viewModel.dismissSummary() }
        )
    }
}
