package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FuelLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelLogDao {

    @Query("SELECT * FROM fuel_log WHERE vehicleId = :vehicleId ORDER BY fillDate DESC, odometerKm DESC")
    fun getAllFuelLogs(vehicleId: Long = 1L): Flow<List<FuelLog>>

    @Query("SELECT * FROM fuel_log WHERE vehicleId = :vehicleId ORDER BY fillDate DESC, odometerKm DESC")
    suspend fun getAllFuelLogsDirect(vehicleId: Long = 1L): List<FuelLog>

    @Query("SELECT * FROM fuel_log WHERE vehicleId = :vehicleId ORDER BY odometerKm DESC LIMIT 1")
    suspend fun getLastFuelLog(vehicleId: Long = 1L): FuelLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLog(fuelLog: FuelLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFuelLogs(fuelLogs: List<FuelLog>)

    @Query("DELETE FROM fuel_log")
    suspend fun clearAllFuelLogs()

    @Update
    suspend fun updateFuelLog(fuelLog: FuelLog)

    @Delete
    suspend fun deleteFuelLog(fuelLog: FuelLog)
}
