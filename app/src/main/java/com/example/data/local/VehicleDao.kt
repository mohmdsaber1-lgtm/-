package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle WHERE id = 1 LIMIT 1")
    fun getVehicleFlow(): Flow<Vehicle?>

    @Query("SELECT * FROM vehicle WHERE id = 1 LIMIT 1")
    suspend fun getVehicleDirect(): Vehicle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVehicle(vehicle: Vehicle)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Query("UPDATE vehicle SET currentOdometer = :odometer, lastOdometerUpdateDate = :updateTime WHERE id = 1")
    suspend fun updateOdometer(odometer: Int, updateTime: Long = System.currentTimeMillis())

    @Query("UPDATE vehicle SET istimaraExpiryDate = :expiryDate WHERE id = 1")
    suspend fun updateIstimaraExpiry(expiryDate: Long)

    @Query("UPDATE vehicle SET fahsExpiryDate = :expiryDate WHERE id = 1")
    suspend fun updateFahsExpiry(expiryDate: Long)

    @Query("UPDATE vehicle SET insuranceExpiryDate = :expiryDate WHERE id = 1")
    suspend fun updateInsuranceExpiry(expiryDate: Long)
}
