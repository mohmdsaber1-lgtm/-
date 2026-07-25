package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PatrolDatabase
import com.example.data.model.FuelLog
import com.example.data.model.MaintenanceStatus
import com.example.data.model.PartSchedule
import com.example.data.model.ServiceLog
import com.example.data.model.TirePressureLog
import com.example.data.model.Vehicle
import com.example.data.repository.PatrolRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardSummary(
    val vehicle: Vehicle,
    val totalPartsCount: Int,
    val dueNowCount: Int,
    val dueSoonCount: Int,
    val goodCount: Int,
    val istimaraDaysLeft: Long,
    val fahsDaysLeft: Long,
    val insuranceDaysLeft: Long,
    val urgentAlerts: List<PartSchedule>
)

class PatrolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PatrolRepository

    init {
        val database = PatrolDatabase.getDatabase(application, viewModelScope)
        repository = PatrolRepository(
            vehicleDao = database.vehicleDao(),
            partScheduleDao = database.partScheduleDao(),
            serviceLogDao = database.serviceLogDao(),
            fuelLogDao = database.fuelLogDao(),
            tirePressureDao = database.tirePressureDao()
        )
    }

    val vehicleState: StateFlow<Vehicle?> = repository.vehicleFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Vehicle()
        )

    val partSchedulesState: StateFlow<List<PartSchedule>> = repository.partSchedulesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val serviceLogsState: StateFlow<List<ServiceLog>> = repository.serviceLogsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val fuelLogsState: StateFlow<List<FuelLog>> = repository.fuelLogsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tirePressureLogsState: StateFlow<List<TirePressureLog>> = repository.tirePressureLogsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalCostState: StateFlow<Double> = repository.totalCostFlow
        .combine(MutableStateFlow(0.0)) { cost, _ -> cost ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val selectedCategory = MutableStateFlow("الكل")

    val dashboardSummaryState: StateFlow<DashboardSummary?> = combine(
        vehicleState,
        partSchedulesState
    ) { vehicle, parts ->
        if (vehicle == null) return@combine null

        val currentKm = vehicle.currentOdometer
        val dueNowList = mutableListOf<PartSchedule>()
        var dueNowCount = 0
        var dueSoonCount = 0
        var goodCount = 0

        parts.forEach { part ->
            when (part.getStatus(currentKm)) {
                MaintenanceStatus.DUE_NOW -> {
                    dueNowCount++
                    dueNowList.add(part)
                }
                MaintenanceStatus.DUE_SOON -> {
                    dueSoonCount++
                    dueNowList.add(part)
                }
                MaintenanceStatus.GOOD -> {
                    goodCount++
                }
            }
        }

        val now = System.currentTimeMillis()
        val istimaraDays = (vehicle.istimaraExpiryDate - now) / (24 * 60 * 60 * 1000)
        val fahsDays = (vehicle.fahsExpiryDate - now) / (24 * 60 * 60 * 1000)
        val insDays = (vehicle.insuranceExpiryDate - now) / (24 * 60 * 60 * 1000)

        DashboardSummary(
            vehicle = vehicle,
            totalPartsCount = parts.size,
            dueNowCount = dueNowCount,
            dueSoonCount = dueSoonCount,
            goodCount = goodCount,
            istimaraDaysLeft = istimaraDays,
            fahsDaysLeft = fahsDays,
            insuranceDaysLeft = insDays,
            urgentAlerts = dueNowList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateOdometer(newKm: Int) {
        viewModelScope.launch {
            repository.updateOdometer(newKm)
            checkAutoNotifications()
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.updateVehicle(vehicle)
        }
    }

    fun updateIstimaraExpiry(dateMillis: Long) {
        viewModelScope.launch {
            repository.updateIstimaraExpiry(dateMillis)
        }
    }

    fun updateFahsExpiry(dateMillis: Long) {
        viewModelScope.launch {
            repository.updateFahsExpiry(dateMillis)
        }
    }

    fun updateInsuranceExpiry(dateMillis: Long) {
        viewModelScope.launch {
            repository.updateInsuranceExpiry(dateMillis)
        }
    }

    fun recordPartServiced(
        partSchedule: PartSchedule,
        serviceKm: Int,
        serviceDate: Long,
        costSar: Double,
        workshopName: String,
        notes: String,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            repository.recordPartServiced(
                partSchedule = partSchedule,
                serviceKm = serviceKm,
                serviceDate = serviceDate,
                costSar = costSar,
                workshopName = workshopName,
                notes = notes,
                imageUri = imageUri
            )
        }
    }

    fun addNewPartSchedule(partSchedule: PartSchedule) {
        viewModelScope.launch {
            repository.addCustomPart(partSchedule)
        }
    }

    fun updatePartSchedule(partSchedule: PartSchedule) {
        viewModelScope.launch {
            repository.updatePartSchedule(partSchedule)
        }
    }

    fun deletePartSchedule(partSchedule: PartSchedule) {
        viewModelScope.launch {
            repository.deletePartSchedule(partSchedule)
        }
    }

    fun addCustomServiceLog(log: ServiceLog) {
        viewModelScope.launch {
            repository.addCustomServiceLog(log)
        }
    }

    fun deleteServiceLog(log: ServiceLog) {
        viewModelScope.launch {
            repository.deleteServiceLog(log)
        }
    }

    fun triggerTestNotification(context: Context) {
        NotificationHelper.sendTestNotification(context)
    }

    fun checkAndSendFahsNotification(context: Context) {
        val summary = dashboardSummaryState.value ?: return
        NotificationHelper.sendFahsAlertNotification(context, summary.fahsDaysLeft)
    }

    fun checkAndSendIstimaraNotification(context: Context) {
        val summary = dashboardSummaryState.value ?: return
        NotificationHelper.sendIstimaraAlertNotification(context, summary.istimaraDaysLeft)
    }

    private fun checkAutoNotifications() {
        val context = getApplication<Application>().applicationContext
        val summary = dashboardSummaryState.value ?: return

        // Auto trigger alerts if Fahs or Istimara is due soon (under 30 days)
        if (summary.fahsDaysLeft in 1..30) {
            NotificationHelper.sendFahsAlertNotification(context, summary.fahsDaysLeft)
        }
        if (summary.istimaraDaysLeft in 1..30) {
            NotificationHelper.sendIstimaraAlertNotification(context, summary.istimaraDaysLeft)
        }
    }

    fun addFuelLog(fuelLog: FuelLog) {
        viewModelScope.launch {
            repository.addFuelLog(fuelLog)
        }
    }

    fun deleteFuelLog(fuelLog: FuelLog) {
        viewModelScope.launch {
            repository.deleteFuelLog(fuelLog)
        }
    }

    fun addTirePressureLog(log: TirePressureLog) {
        viewModelScope.launch {
            repository.addTirePressureLog(log)
        }
    }

    fun deleteTirePressureLog(log: TirePressureLog) {
        viewModelScope.launch {
            repository.deleteTirePressureLog(log)
        }
    }

    fun exportBackup(context: Context) {
        viewModelScope.launch {
            val vehicle = repository.getVehicleDirect()
            val parts = repository.getAllPartSchedulesDirect()
            val logs = repository.getAllServiceLogsDirect()
            val fuel = repository.getAllFuelLogsDirect()
            com.example.utils.DatabaseBackupUtils.exportAndShareBackup(context, vehicle, parts, logs, fuel)
        }
    }

    fun restoreBackup(backupData: com.example.utils.BackupData, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.restoreDatabaseBackup(backupData)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
