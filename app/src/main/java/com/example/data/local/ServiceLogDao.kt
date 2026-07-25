package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ServiceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceLogDao {
    @Query("SELECT * FROM service_log WHERE vehicleId = :vehicleId ORDER BY serviceDate DESC, odometerKm DESC")
    fun getAllServiceLogs(vehicleId: Long = 1L): Flow<List<ServiceLog>>

    @Query("SELECT * FROM service_log WHERE vehicleId = :vehicleId")
    suspend fun getAllServiceLogsDirect(vehicleId: Long = 1L): List<ServiceLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceLog(serviceLog: ServiceLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllServiceLogs(serviceLogs: List<ServiceLog>)

    @Query("DELETE FROM service_log")
    suspend fun clearAllServiceLogs()

    @Update
    suspend fun updateServiceLog(serviceLog: ServiceLog)

    @Delete
    suspend fun deleteServiceLog(serviceLog: ServiceLog)

    @Query("SELECT SUM(costSar) FROM service_log WHERE vehicleId = :vehicleId")
    fun getTotalCostFlow(vehicleId: Long = 1L): Flow<Double?>
}
