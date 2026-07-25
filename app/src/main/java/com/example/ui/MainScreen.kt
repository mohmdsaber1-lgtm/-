package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PartSchedule
import com.example.data.model.ServiceLog
import androidx.compose.material.icons.filled.TireRepair
import com.example.ui.components.EditDocumentDateDialog
import com.example.ui.components.EditPartDialog
import com.example.ui.components.LogFuelDialog
import com.example.ui.components.LogServiceDialog
import com.example.ui.components.LogTirePressureDialog
import com.example.ui.components.NotificationManagerDialog
import com.example.ui.components.OdometerUpdateDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.FuelScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.ServiceHistoryScreen
import com.example.ui.screens.TirePressureScreen
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolGold

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PatrolViewModel) {
    val context = LocalContext.current

    val dashboardSummary by viewModel.dashboardSummaryState.collectAsStateWithLifecycle()
    val partSchedules by viewModel.partSchedulesState.collectAsStateWithLifecycle()
    val serviceLogs by viewModel.serviceLogsState.collectAsStateWithLifecycle()
    val fuelLogs by viewModel.fuelLogsState.collectAsStateWithLifecycle()
    val tireLogs by viewModel.tirePressureLogsState.collectAsStateWithLifecycle()
    val totalCost by viewModel.totalCostState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val vehicle by viewModel.vehicleState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }

    // Dialog state controllers
    var showOdometerDialog by remember { mutableStateOf(false) }
    var showLogServiceDialog by remember { mutableStateOf(false) }
    var selectedPartForService by remember { mutableStateOf<PartSchedule?>(null) }
    var showEditPartDialog by remember { mutableStateOf(false) }
    var showLogFuelDialog by remember { mutableStateOf(false) }
    var showLogTireDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    var showEditDocDialog by remember { mutableStateOf(false) }
    var activeDocName by remember { mutableStateOf("") }
    var activeDocDaysLeft by remember { mutableStateOf(0L) }
    var activeDocType by remember { mutableStateOf("ISTIMARA") }

    val navItems = listOf(
        NavItem("الرئيسية", Icons.Default.DirectionsCar, "nav_dashboard"),
        NavItem("الجدول", Icons.Default.Build, "nav_schedule"),
        NavItem("الإصلاحات", Icons.Default.History, "nav_history"),
        NavItem("الوقود", Icons.Default.LocalGasStation, "nav_fuel"),
        NavItem("الإطارات", Icons.Default.TireRepair, "nav_tires"),
        NavItem("الوثائق", Icons.Default.Assignment, "nav_documents"),
        NavItem("الإحصائيات", Icons.Default.BarChart, "nav_analytics")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "صيانة باترول 2015",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showNotificationDialog = true },
                        modifier = Modifier.testTag("top_bar_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = PatrolGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PatrolDarkCharcoal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = PatrolCardDark,
                contentColor = Color.White
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = (currentTab == index),
                        onClick = { currentTab = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PatrolGold,
                            selectedTextColor = PatrolGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = PatrolGold.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        containerColor = PatrolDarkCharcoal
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    summary = dashboardSummary,
                    latestTireLog = tireLogs.firstOrNull(),
                    onOpenOdometerDialog = { showOdometerDialog = true },
                    onOpenLogServiceDialog = { part ->
                        selectedPartForService = part
                        showLogServiceDialog = true
                    },
                    onNavigateToSchedule = { currentTab = 1 },
                    onNavigateToDocuments = { currentTab = 5 },
                    onNavigateToTires = { currentTab = 4 },
                    onOpenNotificationDialog = { showNotificationDialog = true }
                )

                1 -> ScheduleScreen(
                    partSchedules = partSchedules,
                    currentOdometer = vehicle?.currentOdometer ?: 185000,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectedCategory.value = it },
                    onLogService = { part ->
                        selectedPartForService = part
                        showLogServiceDialog = true
                    },
                    onAddCustomPart = { showEditPartDialog = true }
                )

                2 -> ServiceHistoryScreen(
                    serviceLogs = serviceLogs,
                    totalCost = totalCost,
                    vehicle = vehicle,
                    onAddNewLog = {
                        selectedPartForService = null
                        showLogServiceDialog = true
                    },
                    onDeleteLog = { log ->
                        viewModel.deleteServiceLog(log)
                    }
                )

                3 -> FuelScreen(
                    fuelLogs = fuelLogs,
                    currentOdometer = vehicle?.currentOdometer ?: 185000,
                    onAddFuelClick = { showLogFuelDialog = true },
                    onDeleteFuelLog = { log ->
                        viewModel.deleteFuelLog(log)
                    }
                )

                4 -> TirePressureScreen(
                    tireLogs = tireLogs,
                    vehicle = vehicle,
                    onAddLogClick = { showLogTireDialog = true },
                    onDeleteLogClick = { log ->
                        viewModel.deleteTirePressureLog(log)
                    }
                )

                5 -> DocumentsScreen(
                    vehicle = vehicle,
                    onOpenEditDocDialog = { docName, daysLeft, docType ->
                        activeDocName = docName
                        activeDocDaysLeft = daysLeft
                        activeDocType = docType
                        showEditDocDialog = true
                    },
                    onTestNotification = { ctx ->
                        viewModel.triggerTestNotification(ctx)
                    },
                    onTestFahsNotification = { ctx ->
                        viewModel.checkAndSendFahsNotification(ctx)
                    },
                    onTestIstimaraNotification = { ctx ->
                        viewModel.checkAndSendIstimaraNotification(ctx)
                    },
                    onExportBackup = { ctx ->
                        viewModel.exportBackup(ctx)
                    },
                    onRestoreBackup = { backupData ->
                        viewModel.restoreBackup(backupData) { success ->
                            // Backup restoration complete
                        }
                    }
                )

                6 -> AnalyticsScreen(
                    serviceLogs = serviceLogs,
                    totalCost = totalCost
                )
            }
        }
    }

    // Dialog 1: Update Odometer
    if (showOdometerDialog) {
        OdometerUpdateDialog(
            currentKm = vehicle?.currentOdometer ?: 185000,
            onDismiss = { showOdometerDialog = false },
            onConfirm = { newKm ->
                viewModel.updateOdometer(newKm)
                showOdometerDialog = false
            }
        )
    }

    // Dialog 2: Record Service / Repair Log
    if (showLogServiceDialog) {
        LogServiceDialog(
            partSchedule = selectedPartForService,
            currentVehicleKm = vehicle?.currentOdometer ?: 185000,
            onDismiss = { showLogServiceDialog = false },
            onConfirm = { part, partName, category, serviceKm, serviceDate, cost, workshop, notes, imageUri ->
                if (part != null) {
                    viewModel.recordPartServiced(part, serviceKm, serviceDate, cost, workshop, notes, imageUri)
                } else {
                    viewModel.addCustomServiceLog(
                        ServiceLog(
                            vehicleId = 1L,
                            partName = partName,
                            category = category,
                            serviceDate = serviceDate,
                            odometerKm = serviceKm,
                            costSar = cost,
                            workshopName = workshop,
                            notes = notes,
                            imageUri = imageUri
                        )
                    )
                }
                showLogServiceDialog = false
            }
        )
    }

    // Dialog 3: Add Custom Part Schedule
    if (showEditPartDialog) {
        EditPartDialog(
            currentKm = vehicle?.currentOdometer ?: 185000,
            onDismiss = { showEditPartDialog = false },
            onConfirm = { newPart ->
                viewModel.addNewPartSchedule(newPart)
                showEditPartDialog = false
            }
        )
    }

    // Dialog 4: Edit Istimara / Fahs Expiration Days
    if (showEditDocDialog) {
        EditDocumentDateDialog(
            documentName = activeDocName,
            currentDaysLeft = activeDocDaysLeft,
            onDismiss = { showEditDocDialog = false },
            onConfirm = { futureTimeMillis ->
                when (activeDocType) {
                    "ISTIMARA" -> viewModel.updateIstimaraExpiry(futureTimeMillis)
                    "FAHS" -> viewModel.updateFahsExpiry(futureTimeMillis)
                    "INSURANCE" -> viewModel.updateInsuranceExpiry(futureTimeMillis)
                }
                showEditDocDialog = false
            }
        )
    }

    // Dialog 5: Log Fuel Refuel
    if (showLogFuelDialog) {
        val lastFuelLog = fuelLogs.maxByOrNull { it.odometerKm }
        LogFuelDialog(
            currentVehicleKm = vehicle?.currentOdometer ?: 185000,
            lastFuelOdometerKm = lastFuelLog?.odometerKm,
            onDismiss = { showLogFuelDialog = false },
            onConfirm = { newFuelLog ->
                viewModel.addFuelLog(newFuelLog)
                showLogFuelDialog = false
            }
        )
    }

    // Dialog 6: Notification Manager Dialog
    if (showNotificationDialog) {
        NotificationManagerDialog(
            vehicle = vehicle,
            partSchedules = partSchedules,
            onDismiss = { showNotificationDialog = false }
        )
    }

    // Dialog 7: Log Tire Pressure Dialog
    if (showLogTireDialog) {
        LogTirePressureDialog(
            currentVehicleKm = vehicle?.currentOdometer ?: 185000,
            onDismiss = { showLogTireDialog = false },
            onConfirm = { tireLog ->
                viewModel.addTirePressureLog(tireLog)
                showLogTireDialog = false
            }
        )
    }
}
