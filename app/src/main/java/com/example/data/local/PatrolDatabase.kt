package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FuelLog
import com.example.data.model.PartSchedule
import com.example.data.model.ServiceLog
import com.example.data.model.TirePressureLog
import com.example.data.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Vehicle::class, PartSchedule::class, ServiceLog::class, FuelLog::class, TirePressureLog::class],
    version = 4,
    exportSchema = false
)
abstract class PatrolDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun partScheduleDao(): PartScheduleDao
    abstract fun serviceLogDao(): ServiceLogDao
    abstract fun fuelLogDao(): FuelLogDao
    abstract fun tirePressureDao(): TirePressureDao

    companion object {
        @Volatile
        private var INSTANCE: PatrolDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PatrolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PatrolDatabase::class.java,
                    "patrol_maintenance_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: PatrolDatabase) {
            val now = System.currentTimeMillis()
            val initialKm = 185000

            // 1. Initial Vehicle Profile
            db.vehicleDao().insertOrUpdateVehicle(
                Vehicle(
                    id = 1L,
                    name = "نيسان باترول 2015",
                    modelYear = 2015,
                    engine = "V8 5.6L VK56",
                    trim = "بلاتينيوم LE",
                    plateNumber = "أ ب ج 1234",
                    currentOdometer = initialKm,
                    istimaraExpiryDate = now + (90L * 24 * 60 * 60 * 1000), // 90 days from now
                    fahsExpiryDate = now + (25L * 24 * 60 * 60 * 1000),     // 25 days from now (DUE SOON)
                    insuranceExpiryDate = now + (180L * 24 * 60 * 60 * 1000)
                )
            )

            // 2. Pre-populated Agency Maintenance Schedule for Nissan Patrol 2015
            val presetSchedule = listOf(
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "زيت وفلتر المحرك",
                    partNameEn = "Engine Oil & Filter",
                    category = "زيوت وسوائل",
                    intervalKm = 10000,
                    intervalMonths = 6,
                    lastServiceKm = 178000,
                    lastServiceDate = now - (100L * 24 * 60 * 60 * 1000),
                    specification = "زيت تخليقي 5W-30 (7.2 لتر) مع فلتر نيسان الأصلي"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "فلتر الهواء (مزدوج)",
                    partNameEn = "Air Cleaner Element",
                    category = "فلاتر",
                    intervalKm = 20000,
                    intervalMonths = 12,
                    lastServiceKm = 170000,
                    lastServiceDate = now - (180L * 24 * 60 * 60 * 1000),
                    specification = "فلاتر هواء نيسان أصلية محرك V8"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "فلتر المكيف (المقصورة)",
                    partNameEn = "Cabin Air Filter",
                    category = "فلاتر",
                    intervalKm = 20000,
                    intervalMonths = 12,
                    lastServiceKm = 165000,
                    lastServiceDate = now - (200L * 24 * 60 * 60 * 1000),
                    specification = "فلتر كربون منقي لهواء التكييف"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "شمعات الإشغال (البواجي)",
                    partNameEn = "Spark Plugs (Laser Iridium)",
                    category = "محرك وشمعات",
                    intervalKm = 80000,
                    intervalMonths = 48,
                    lastServiceKm = 100000,
                    lastServiceDate = now - (400L * 24 * 60 * 60 * 1000),
                    specification = "8 بواجي إيريديوم ليزر نيسان أصلية"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "زيت القير الأوتوماتيكي (ATF)",
                    partNameEn = "Automatic Transmission Fluid",
                    category = "قير ودفرنش",
                    intervalKm = 40000,
                    intervalMonths = 24,
                    lastServiceKm = 145000,
                    lastServiceDate = now - (300L * 24 * 60 * 60 * 1000),
                    specification = "زيت نيسان Matic-S الأصلي (القير 7 سرعات)"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "زيت الدفرنش والدبل",
                    partNameEn = "Differential & Transfer Gear Oil",
                    category = "قير ودفرنش",
                    intervalKm = 40000,
                    intervalMonths = 24,
                    lastServiceKm = 145000,
                    lastServiceDate = now - (300L * 24 * 60 * 60 * 1000),
                    specification = "زيت دفرنش نيسان 80W-90 و 75W-90"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "سائل الفرامل",
                    partNameEn = "Brake Fluid",
                    category = "فرامل",
                    intervalKm = 40000,
                    intervalMonths = 24,
                    lastServiceKm = 150000,
                    lastServiceDate = now - (250L * 24 * 60 * 60 * 1000),
                    specification = "سائل فرامل DOT 4 نيسان"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "سائل تبريد المحرك (الرديتر)",
                    partNameEn = "Engine Coolant",
                    category = "زيوت وسوائل",
                    intervalKm = 80000,
                    intervalMonths = 48,
                    lastServiceKm = 120000,
                    lastServiceDate = now - (350L * 24 * 60 * 60 * 1000),
                    specification = "ماء رديتر نيسان الأزرق Long Life"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "فحمات الفرامل الأمامية",
                    partNameEn = "Front Brake Pads",
                    category = "فرامل",
                    intervalKm = 30000,
                    intervalMonths = 18,
                    lastServiceKm = 156000,
                    lastServiceDate = now - (210L * 24 * 60 * 60 * 1000),
                    specification = "أقمشة فرامل سيراميك أصلية نيسان"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "فحمات الفرامل الخلفية",
                    partNameEn = "Rear Brake Pads",
                    category = "فرامل",
                    intervalKm = 40000,
                    intervalMonths = 24,
                    lastServiceKm = 150000,
                    lastServiceDate = now - (250L * 24 * 60 * 60 * 1000),
                    specification = "أقمشة فرامل خلفية نيسان"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "فلتر البنزين / الوقود",
                    partNameEn = "Fuel Filter",
                    category = "فلاتر",
                    intervalKm = 80000,
                    intervalMonths = 48,
                    lastServiceKm = 100000,
                    lastServiceDate = now - (400L * 24 * 60 * 60 * 1000),
                    specification = "فلتر بنزين مضخة الوقود التانكي"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "سير المحرك والمكيف",
                    partNameEn = "Serpentine Drive Belt",
                    category = "محرك وشمعات",
                    intervalKm = 60000,
                    intervalMonths = 36,
                    lastServiceKm = 125000,
                    lastServiceDate = now - (320L * 24 * 60 * 60 * 1000),
                    specification = "سير مكينة نيسان أصلي"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "بطارية السيارة",
                    partNameEn = "Car Battery",
                    category = "كهرباء وبطارية",
                    intervalKm = 30000,
                    intervalMonths = 24,
                    lastServiceKm = 160000,
                    lastServiceDate = now - (220L * 24 * 60 * 60 * 1000),
                    specification = "بطارية 80 أمبير 12 فولت"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "تدوير وموازنة الإطارات",
                    partNameEn = "Tire Rotation & Alignment",
                    category = "إطارات وهيكل",
                    intervalKm = 10000,
                    intervalMonths = 6,
                    lastServiceKm = 175000,
                    lastServiceDate = now - (120L * 24 * 60 * 60 * 1000),
                    specification = "تدوير 5 إطارات وترصيص ليزر"
                ),
                PartSchedule(
                    vehicleId = 1L,
                    partNameAr = "تنظيف البخاخات وحساس MAF",
                    partNameEn = "Injector Clean & MAF Sensor",
                    category = "محرك وشمعات",
                    intervalKm = 40000,
                    intervalMonths = 24,
                    lastServiceKm = 145000,
                    lastServiceDate = now - (280L * 24 * 60 * 60 * 1000),
                    specification = "منظف بخاخات أصلي مع بخاخ حساس الهواء"
                )
            )

            db.partScheduleDao().insertAllPartSchedules(presetSchedule)

            // 3. Initial Sample Repair Logs
            val sampleLogs = listOf(
                ServiceLog(
                    vehicleId = 1L,
                    partName = "زيت وفلتر المحرك",
                    category = "زيوت وسوائل",
                    serviceDate = now - (100L * 24 * 60 * 60 * 1000),
                    odometerKm = 178000,
                    costSar = 380.0,
                    workshopName = "بترومين اكسبريس نيسان",
                    serviceType = "صيانة دورية",
                    notes = "تغيير زيت تخليقي 5W-30 مع فلتر أصلي"
                ),
                ServiceLog(
                    vehicleId = 1L,
                    partName = "فحمات الفرامل الأمامية",
                    category = "فرامل",
                    serviceDate = now - (210L * 24 * 60 * 60 * 1000),
                    odometerKm = 156000,
                    costSar = 520.0,
                    workshopName = "مركز صيانة الباترول",
                    serviceType = "تغيير قطع",
                    notes = "خرط هوبات أمامية وتغيير أقمشة أصلية"
                ),
                ServiceLog(
                    vehicleId = 1L,
                    partName = "بطارية السيارة",
                    category = "كهرباء وبطارية",
                    serviceDate = now - (220L * 24 * 60 * 60 * 1000),
                    odometerKm = 160000,
                    costSar = 450.0,
                    workshopName = "محل بطاريات",
                    serviceType = "تغيير قطع",
                    notes = "تركيب بطارية هانكوك 80 أمبير مع ضمان سنة"
                )
            )

            sampleLogs.forEach { db.serviceLogDao().insertServiceLog(it) }

            // 4. Initial Fuel Refuel Logs for Patrol 2015 V8
            val sampleFuelLogs = listOf(
                FuelLog(
                    vehicleId = 1L,
                    fillDate = now - (35L * 24 * 60 * 60 * 1000),
                    odometerKm = 182600,
                    fuelLiters = 88.0,
                    pricePerLiter = 2.18,
                    totalCostSar = 191.84,
                    distanceDrivenKm = 580,
                    fuelType = "بنزين 91",
                    isFullTank = true,
                    notes = "تعبئة كاملة - قيادة داخل المدينة"
                ),
                FuelLog(
                    vehicleId = 1L,
                    fillDate = now - (25L * 24 * 60 * 60 * 1000),
                    odometerKm = 183250,
                    fuelLiters = 92.5,
                    pricePerLiter = 2.18,
                    totalCostSar = 201.65,
                    distanceDrivenKm = 650,
                    fuelType = "بنزين 91",
                    isFullTank = true,
                    notes = "خط سفر الرياض - الدمام"
                ),
                FuelLog(
                    vehicleId = 1L,
                    fillDate = now - (14L * 24 * 60 * 60 * 1000),
                    odometerKm = 183880,
                    fuelLiters = 95.0,
                    pricePerLiter = 2.18,
                    totalCostSar = 207.10,
                    distanceDrivenKm = 630,
                    fuelType = "بنزين 91",
                    isFullTank = true,
                    notes = "تعبئة قبل رحلة برية"
                ),
                FuelLog(
                    vehicleId = 1L,
                    fillDate = now - (3L * 24 * 60 * 60 * 1000),
                    odometerKm = 184500,
                    fuelLiters = 90.0,
                    pricePerLiter = 2.18,
                    totalCostSar = 196.20,
                    distanceDrivenKm = 620,
                    fuelType = "بنزين 91",
                    isFullTank = true,
                    notes = "فل كامل - محطة الدريس"
                )
            )

            sampleFuelLogs.forEach { db.fuelLogDao().insertFuelLog(it) }

            // 5. Initial Tire Pressure Logs for Nissan Patrol 2015
            val sampleTireLogs = listOf(
                TirePressureLog(
                    vehicleId = 1L,
                    recordDate = now - (20L * 24 * 60 * 60 * 1000),
                    odometerKm = 183500,
                    frontLeftPsi = 35.0,
                    frontRightPsi = 35.0,
                    rearLeftPsi = 35.0,
                    rearRightPsi = 35.0,
                    sparePsi = 35.0,
                    drivingContext = "مدينة",
                    notes = "تأكيد ضغط الوكالة 35 PSI هواء نيتروجين"
                ),
                TirePressureLog(
                    vehicleId = 1L,
                    recordDate = now - (10L * 24 * 60 * 60 * 1000),
                    odometerKm = 184100,
                    frontLeftPsi = 18.0,
                    frontRightPsi = 18.0,
                    rearLeftPsi = 18.0,
                    rearRightPsi = 18.0,
                    sparePsi = 35.0,
                    drivingContext = "رحلة برية / رمل",
                    notes = "تنفيس إطارات الباترول لدخول النفود والطعوس"
                ),
                TirePressureLog(
                    vehicleId = 1L,
                    recordDate = now - (2L * 24 * 60 * 60 * 1000),
                    odometerKm = 184800,
                    frontLeftPsi = 35.0,
                    frontRightPsi = 35.0,
                    rearLeftPsi = 35.0,
                    rearRightPsi = 35.0,
                    sparePsi = 35.0,
                    drivingContext = "خط وسفر",
                    notes = "إعادة تعبئة الضغط بعد الرحلة البرية وقبل السفر"
                )
            )

            sampleTireLogs.forEach { db.tirePressureDao().insertTirePressureLog(it) }
        }
    }
}
