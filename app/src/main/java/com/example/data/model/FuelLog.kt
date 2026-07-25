package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_log")
data class FuelLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long = 1L,
    val fillDate: Long = System.currentTimeMillis(),
    val odometerKm: Int,
    val fuelLiters: Double,
    val pricePerLiter: Double = 2.18,
    val totalCostSar: Double = fuelLiters * pricePerLiter,
    val distanceDrivenKm: Int = 0,
    val fuelType: String = "بنزين 91",
    val isFullTank: Boolean = true,
    val notes: String = ""
) {
    val litersPer100Km: Double
        get() = if (distanceDrivenKm > 0) (fuelLiters / distanceDrivenKm) * 100 else 0.0

    val kmPerLiter: Double
        get() = if (fuelLiters > 0 && distanceDrivenKm > 0) distanceDrivenKm / fuelLiters else 0.0

    val costPerKm: Double
        get() = if (distanceDrivenKm > 0) totalCostSar / distanceDrivenKm else 0.0
}
