package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PartSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface PartScheduleDao {
    @Query("SELECT * FROM part_schedule WHERE vehicleId = :vehicleId ORDER BY category ASC, partNameAr ASC")
    fun getAllPartSchedules(vehicleId: Long = 1L): Flow<List<PartSchedule>>

    @Query("SELECT * FROM part_schedule WHERE vehicleId = :vehicleId")
    suspend fun getAllPartSchedulesDirect(vehicleId: Long = 1L): List<PartSchedule>

    @Query("SELECT * FROM part_schedule WHERE id = :id LIMIT 1")
    suspend fun getPartScheduleById(id: Long): PartSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartSchedule(partSchedule: PartSchedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPartSchedules(partSchedules: List<PartSchedule>)

    @Query("DELETE FROM part_schedule")
    suspend fun clearAllPartSchedules()

    @Update
    suspend fun updatePartSchedule(partSchedule: PartSchedule)

    @Delete
    suspend fun deletePartSchedule(partSchedule: PartSchedule)

    @Query("UPDATE part_schedule SET lastServiceKm = :serviceKm, lastServiceDate = :serviceDate WHERE id = :partId")
    suspend fun recordPartServiced(partId: Long, serviceKm: Int, serviceDate: Long)

    @Query("SELECT COUNT(*) FROM part_schedule")
    suspend fun getCount(): Int
}
