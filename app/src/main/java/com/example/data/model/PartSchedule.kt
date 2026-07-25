package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "part_schedule")
data class PartSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long = 1L,
    val partNameAr: String,
    val partNameEn: String = "",
    val category: String, // "زيوت وسوائل", "فلاتر", "فرامل", "محرك وشمعات", "قير ودفرنش", "كهرباء وبطارية", "إطارات وهيكل"
    val intervalKm: Int,
    val intervalMonths: Int,
    val lastServiceKm: Int,
    val lastServiceDate: Long,
    val specification: String = "",
    val notes: String = "",
    val isCustomPart: Boolean = false
) {
    fun getNextDueKm(): Int = lastServiceKm + intervalKm

    fun getNextDueDate(): Long = lastServiceDate + (intervalMonths.toLong() * 30L * 24 * 60 * 60 * 1000)

    fun getRemainingKm(currentOdometer: Int): Int = getNextDueKm() - currentOdometer

    fun getRemainingDays(): Long {
        val nextDate = getNextDueDate()
        val diff = nextDate - System.currentTimeMillis()
        return diff / (24 * 60 * 60 * 1000)
    }

    fun getStatus(currentOdometer: Int): MaintenanceStatus {
        val remKm = getRemainingKm(currentOdometer)
        val remDays = getRemainingDays()

        return when {
            remKm <= 0 || remDays <= 0 -> MaintenanceStatus.DUE_NOW
            remKm <= 1000 || remDays <= 15 -> MaintenanceStatus.DUE_SOON
            else -> MaintenanceStatus.GOOD
        }
    }
}

enum class MaintenanceStatus {
    DUE_NOW,   // مستحق الآن (تنبيه أحمر)
    DUE_SOON,  // قريباً (تنبيه أصفر)
    GOOD       // حالة ممتازة (أخضر)
}
