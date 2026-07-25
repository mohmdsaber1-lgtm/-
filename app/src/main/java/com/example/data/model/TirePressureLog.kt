package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tire_pressure_log")
data class TirePressureLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long = 1L,
    val recordDate: Long = System.currentTimeMillis(),
    val odometerKm: Int,
    val frontLeftPsi: Double = 35.0,
    val frontRightPsi: Double = 35.0,
    val rearLeftPsi: Double = 35.0,
    val rearRightPsi: Double = 35.0,
    val sparePsi: Double = 35.0,
    val drivingContext: String = "مدينة",
    val notes: String = ""
) {
    val averagePsi: Double
        get() = (frontLeftPsi + frontRightPsi + rearLeftPsi + rearRightPsi) / 4.0

    fun getStatusForPsi(psi: Double): TireStatus {
        return when {
            psi < 20.0 -> TireStatus.OFFROAD_LOW
            psi < 31.0 -> TireStatus.LOW
            psi > 39.0 -> TireStatus.HIGH
            else -> TireStatus.OPTIMAL
        }
    }
}

enum class TireStatus {
    OPTIMAL,
    LOW,
    OFFROAD_LOW,
    HIGH
}
