package com.example.data.repository

import com.example.data.local.FuelLogDao
import com.example.data.local.PartScheduleDao
import com.example.data.local.ServiceLogDao
import com.example.data.local.TirePressureDao
import com.example.data.local.VehicleDao
import com.example.data.model.FuelLog
import com.example.data.model.PartSchedule
import com.example.data.model.ServiceLog
import com.example.data.model.TirePressureLog
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

class PatrolRepository(
    private val vehicleDao: VehicleDao,
    private val partScheduleDao: PartScheduleDao,
    private val serviceLogDao: ServiceLogDao,
    private val fuelLogDao: FuelLogDao,
    private val tirePressureDao: TirePressureDao
) {
    val vehicleFlow: Flow<Vehicle?> = vehicleDao.getVehicleFlow()
    val partSchedulesFlow: Flow<List<PartSchedule>> = partScheduleDao.getAllPartSchedules(1L)
    val serviceLogsFlow: Flow<List<ServiceLog>> = serviceLogDao.getAllServiceLogs(1L)
    val totalCostFlow: Flow<Double?> = serviceLogDao.getTotalCostFlow(1L)
    val fuelLogsFlow: Flow<List<FuelLog>> = fuelLogDao.getAllFuelLogs(1L)
    val tirePressureLogsFlow: Flow<List<TirePressureLog>> = tirePressureDao.getAllTirePressureLogs(1L)

    suspend fun getVehicleDirect(): Vehicle? = vehicleDao.getVehicleDirect()

    suspend fun updateOdometer(newKm: Int) {
        vehicleDao.updateOdometer(newKm)
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.updateVehicle(vehicle)
    }

    suspend fun updateIstimaraExpiry(dateMillis: Long) {
        vehicleDao.updateIstimaraExpiry(dateMillis)
    }

    suspend fun updateFahsExpiry(dateMillis: Long) {
        vehicleDao.updateFahsExpiry(dateMillis)
    }

    suspend fun updateInsuranceExpiry(dateMillis: Long) {
        vehicleDao.updateInsuranceExpiry(dateMillis)
    }

    suspend fun recordPartServiced(
        partSchedule: PartSchedule,
        serviceKm: Int,
        serviceDate: Long,
        costSar: Double,
        workshopName: String,
        notes: String,
        imageUri: String? = null
    ) {
        // 1. Update the part schedule's last service info
        partScheduleDao.recordPartServiced(partSchedule.id, serviceKm, serviceDate)

        // 2. Also check if the new service odometer is higher than vehicle current odometer and update if so
        val vehicle = vehicleDao.getVehicleDirect()
        if (vehicle != null && serviceKm > vehicle.currentOdometer) {
            vehicleDao.updateOdometer(serviceKm)
        }

        // 3. Add a record in service logs
        val serviceLog = ServiceLog(
            partScheduleId = partSchedule.id,
            vehicleId = 1L,
            partName = partSchedule.partNameAr,
            category = partSchedule.category,
            serviceDate = serviceDate,
            odometerKm = serviceKm,
            costSar = costSar,
            workshopName = workshopName,
            serviceType = "صيانة دورية",
            notes = notes,
            imageUri = imageUri
        )
        serviceLogDao.insertServiceLog(serviceLog)
    }

    suspend fun addCustomPart(partSchedule: PartSchedule) {
        partScheduleDao.insertPartSchedule(partSchedule)
    }

    suspend fun updatePartSchedule(partSchedule: PartSchedule) {
        partScheduleDao.updatePartSchedule(partSchedule)
    }

    suspend fun deletePartSchedule(partSchedule: PartSchedule) {
        partScheduleDao.deletePartSchedule(partSchedule)
    }

    suspend fun addCustomServiceLog(log: ServiceLog) {
        serviceLogDao.insertServiceLog(log)
    }

    suspend fun deleteServiceLog(log: ServiceLog) {
        serviceLogDao.deleteServiceLog(log)
    }

    suspend fun addFuelLog(fuelLog: FuelLog): Long {
        val lastLog = fuelLogDao.getLastFuelLog(1L)
        var updatedLog = fuelLog

        // Calculate distance driven automatically if not specified and previous log exists
        if (fuelLog.distanceDrivenKm <= 0 && lastLog != null && fuelLog.odometerKm > lastLog.odometerKm) {
            updatedLog = fuelLog.copy(distanceDrivenKm = fuelLog.odometerKm - lastLog.odometerKm)
        }

        // Also update vehicle current odometer if higher
        val vehicle = vehicleDao.getVehicleDirect()
        if (vehicle != null && updatedLog.odometerKm > vehicle.currentOdometer) {
            vehicleDao.updateOdometer(updatedLog.odometerKm)
        }

        return fuelLogDao.insertFuelLog(updatedLog)
    }

    suspend fun deleteFuelLog(fuelLog: FuelLog) {
        fuelLogDao.deleteFuelLog(fuelLog)
    }

    suspend fun getLastFuelLog(): FuelLog? {
        return fuelLogDao.getLastFuelLog(1L)
    }

    suspend fun getAllFuelLogsDirect(): List<FuelLog> {
        return fuelLogDao.getAllFuelLogsDirect(1L)
    }

    suspend fun restoreDatabaseBackup(backupData: com.example.utils.BackupData) {
        if (backupData.vehicle != null) {
            vehicleDao.insertOrUpdateVehicle(backupData.vehicle)
        }
        if (backupData.partSchedules.isNotEmpty()) {
            partScheduleDao.clearAllPartSchedules()
            partScheduleDao.insertAllPartSchedules(backupData.partSchedules)
        }
        if (backupData.serviceLogs.isNotEmpty()) {
            serviceLogDao.clearAllServiceLogs()
            serviceLogDao.insertAllServiceLogs(backupData.serviceLogs)
        }
        if (backupData.fuelLogs.isNotEmpty()) {
            fuelLogDao.clearAllFuelLogs()
            fuelLogDao.insertAllFuelLogs(backupData.fuelLogs)
        }
    }

    suspend fun addTirePressureLog(log: TirePressureLog): Long {
        val vehicle = vehicleDao.getVehicleDirect()
        if (vehicle != null && log.odometerKm > vehicle.currentOdometer) {
            vehicleDao.updateOdometer(log.odometerKm)
        }
        return tirePressureDao.insertTirePressureLog(log)
    }

    suspend fun deleteTirePressureLog(log: TirePressureLog) {
        tirePressureDao.deleteTirePressureLog(log)
    }

    suspend fun getLastTirePressureLog(): TirePressureLog? {
        return tirePressureDao.getLastTirePressureLog(1L)
    }

    suspend fun getAllPartSchedulesDirect(): List<PartSchedule> {
        return partScheduleDao.getAllPartSchedulesDirect()
    }

    suspend fun getAllServiceLogsDirect(): List<ServiceLog> {
        return serviceLogDao.getAllServiceLogsDirect()
    }
}
