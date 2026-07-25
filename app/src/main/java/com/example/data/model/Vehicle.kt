package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle")
data class Vehicle(
    @PrimaryKey val id: Long = 1L,
    val name: String = "نيسان باترول 2015",
    val modelYear: Int = 2015,
    val engine: String = "V8 5.6L VK56",
    val trim: String = "بلاتينيوم / SE",
    val plateNumber: String = "أ ب ج 1234",
    val currentOdometer: Int = 185000,
    val istimaraExpiryDate: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000), // 90 days from now
    val fahsExpiryDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),     // 30 days from now
    val insuranceExpiryDate: Long = System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000),// 120 days from now
    val lastOdometerUpdateDate: Long = System.currentTimeMillis()
)
