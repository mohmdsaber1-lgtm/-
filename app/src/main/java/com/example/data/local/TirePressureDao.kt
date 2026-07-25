package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TirePressureLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TirePressureDao {

    @Query("SELECT * FROM tire_pressure_log WHERE vehicleId = :vehicleId ORDER BY recordDate DESC, odometerKm DESC")
    fun getAllTirePressureLogs(vehicleId: Long = 1L): Flow<List<TirePressureLog>>

    @Query("SELECT * FROM tire_pressure_log WHERE vehicleId = :vehicleId ORDER BY recordDate DESC, odometerKm DESC")
    suspend fun getAllTirePressureLogsDirect(vehicleId: Long = 1L): List<TirePressureLog>

    @Query("SELECT * FROM tire_pressure_log WHERE vehicleId = :vehicleId ORDER BY recordDate DESC, odometerKm DESC LIMIT 1")
    suspend fun getLastTirePressureLog(vehicleId: Long = 1L): TirePressureLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTirePressureLog(log: TirePressureLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTirePressureLogs(logs: List<TirePressureLog>)

    @Query("DELETE FROM tire_pressure_log")
    suspend fun clearAllTirePressureLogs()

    @Update
    suspend fun updateTirePressureLog(log: TirePressureLog)

    @Delete
    suspend fun deleteTirePressureLog(log: TirePressureLog)
}
