package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_log")
data class ServiceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val partScheduleId: Long? = null,
    val vehicleId: Long = 1L,
    val partName: String,
    val category: String,
    val serviceDate: Long,
    val odometerKm: Int,
    val costSar: Double,
    val workshopName: String = "",
    val serviceType: String = "صيانة دورية", // "صيانة دورية", "تغيير قطع", "إصلاح عطل"
    val invoiceNumber: String = "",
    val notes: String = "",
    val imageUri: String? = null
)
