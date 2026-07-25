package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.FuelLog
import com.example.data.model.PartSchedule
import com.example.data.model.ServiceLog
import com.example.data.model.TirePressureLog
import com.example.data.model.Vehicle
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class BackupData(
    val vehicle: Vehicle?,
    val partSchedules: List<PartSchedule>,
    val serviceLogs: List<ServiceLog>,
    val fuelLogs: List<FuelLog> = emptyList(),
    val tireLogs: List<TirePressureLog> = emptyList()
)

object DatabaseBackupUtils {

    fun generateBackupJson(
        vehicle: Vehicle?,
        partSchedules: List<PartSchedule>,
        serviceLogs: List<ServiceLog>,
        fuelLogs: List<FuelLog> = emptyList()
    ): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("timestamp", System.currentTimeMillis())
        root.put("appName", "Patrol Maintenance")

        // Vehicle
        if (vehicle != null) {
            val vObj = JSONObject().apply {
                put("id", vehicle.id)
                put("name", vehicle.name)
                put("modelYear", vehicle.modelYear)
                put("engine", vehicle.engine)
                put("trim", vehicle.trim)
                put("plateNumber", vehicle.plateNumber)
                put("currentOdometer", vehicle.currentOdometer)
                put("istimaraExpiryDate", vehicle.istimaraExpiryDate)
                put("fahsExpiryDate", vehicle.fahsExpiryDate)
                put("insuranceExpiryDate", vehicle.insuranceExpiryDate)
                put("lastOdometerUpdateDate", vehicle.lastOdometerUpdateDate)
            }
            root.put("vehicle", vObj)
        }

        // Part Schedules
        val partsArray = JSONArray()
        partSchedules.forEach { part ->
            val pObj = JSONObject().apply {
                put("id", part.id)
                put("vehicleId", part.vehicleId)
                put("partNameAr", part.partNameAr)
                put("partNameEn", part.partNameEn)
                put("category", part.category)
                put("intervalKm", part.intervalKm)
                put("intervalMonths", part.intervalMonths)
                put("lastServiceKm", part.lastServiceKm)
                put("lastServiceDate", part.lastServiceDate)
                put("specification", part.specification)
                put("notes", part.notes)
                put("isCustomPart", part.isCustomPart)
            }
            partsArray.put(pObj)
        }
        root.put("partSchedules", partsArray)

        // Service Logs
        val logsArray = JSONArray()
        serviceLogs.forEach { log ->
            val lObj = JSONObject().apply {
                put("id", log.id)
                if (log.partScheduleId != null) put("partScheduleId", log.partScheduleId)
                put("vehicleId", log.vehicleId)
                put("partName", log.partName)
                put("category", log.category)
                put("serviceDate", log.serviceDate)
                put("odometerKm", log.odometerKm)
                put("costSar", log.costSar)
                put("workshopName", log.workshopName)
                put("serviceType", log.serviceType)
                put("invoiceNumber", log.invoiceNumber)
                put("notes", log.notes)
                if (log.imageUri != null) put("imageUri", log.imageUri)
            }
            logsArray.put(lObj)
        }
        root.put("serviceLogs", logsArray)

        // Fuel Logs
        val fuelArray = JSONArray()
        fuelLogs.forEach { fuel ->
            val fObj = JSONObject().apply {
                put("id", fuel.id)
                put("vehicleId", fuel.vehicleId)
                put("fillDate", fuel.fillDate)
                put("odometerKm", fuel.odometerKm)
                put("fuelLiters", fuel.fuelLiters)
                put("pricePerLiter", fuel.pricePerLiter)
                put("totalCostSar", fuel.totalCostSar)
                put("distanceDrivenKm", fuel.distanceDrivenKm)
                put("fuelType", fuel.fuelType)
                put("isFullTank", fuel.isFullTank)
                put("notes", fuel.notes)
            }
            fuelArray.put(fObj)
        }
        root.put("fuelLogs", fuelArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)

        // Vehicle
        var vehicle: Vehicle? = null
        if (root.has("vehicle") && !root.isNull("vehicle")) {
            val vObj = root.getJSONObject("vehicle")
            vehicle = Vehicle(
                id = vObj.optLong("id", 1L),
                name = vObj.optString("name", "نيسان باترول 2015"),
                modelYear = vObj.optInt("modelYear", 2015),
                engine = vObj.optString("engine", "V8 5.6L VK56"),
                trim = vObj.optString("trim", "بلاتينيوم / SE"),
                plateNumber = vObj.optString("plateNumber", "أ ب ج 1234"),
                currentOdometer = vObj.optInt("currentOdometer", 185000),
                istimaraExpiryDate = vObj.optLong("istimaraExpiryDate", System.currentTimeMillis()),
                fahsExpiryDate = vObj.optLong("fahsExpiryDate", System.currentTimeMillis()),
                insuranceExpiryDate = vObj.optLong("insuranceExpiryDate", System.currentTimeMillis()),
                lastOdometerUpdateDate = vObj.optLong("lastOdometerUpdateDate", System.currentTimeMillis())
            )
        }

        // Part Schedules
        val parts = mutableListOf<PartSchedule>()
        if (root.has("partSchedules")) {
            val partsArray = root.getJSONArray("partSchedules")
            for (i in 0 until partsArray.length()) {
                val pObj = partsArray.getJSONObject(i)
                val part = PartSchedule(
                    id = pObj.optLong("id", 0L),
                    vehicleId = pObj.optLong("vehicleId", 1L),
                    partNameAr = pObj.optString("partNameAr", ""),
                    partNameEn = pObj.optString("partNameEn", ""),
                    category = pObj.optString("category", "عام"),
                    intervalKm = pObj.optInt("intervalKm", 10000),
                    intervalMonths = pObj.optInt("intervalMonths", 6),
                    lastServiceKm = pObj.optInt("lastServiceKm", 0),
                    lastServiceDate = pObj.optLong("lastServiceDate", System.currentTimeMillis()),
                    specification = pObj.optString("specification", ""),
                    notes = pObj.optString("notes", ""),
                    isCustomPart = pObj.optBoolean("isCustomPart", false)
                )
                parts.add(part)
            }
        }

        // Service Logs
        val logs = mutableListOf<ServiceLog>()
        if (root.has("serviceLogs")) {
            val logsArray = root.getJSONArray("serviceLogs")
            for (i in 0 until logsArray.length()) {
                val lObj = logsArray.getJSONObject(i)
                val partScheduleId = if (lObj.has("partScheduleId") && !lObj.isNull("partScheduleId")) {
                    lObj.getLong("partScheduleId")
                } else null

                val imageUri = if (lObj.has("imageUri") && !lObj.isNull("imageUri")) {
                    lObj.getString("imageUri")
                } else null

                val log = ServiceLog(
                    id = lObj.optLong("id", 0L),
                    partScheduleId = partScheduleId,
                    vehicleId = lObj.optLong("vehicleId", 1L),
                    partName = lObj.optString("partName", ""),
                    category = lObj.optString("category", "عام"),
                    serviceDate = lObj.optLong("serviceDate", System.currentTimeMillis()),
                    odometerKm = lObj.optInt("odometerKm", 0),
                    costSar = lObj.optDouble("costSar", 0.0),
                    workshopName = lObj.optString("workshopName", ""),
                    serviceType = lObj.optString("serviceType", "صيانة دورية"),
                    invoiceNumber = lObj.optString("invoiceNumber", ""),
                    notes = lObj.optString("notes", ""),
                    imageUri = imageUri
                )
                logs.add(log)
            }
        }

        // Fuel Logs
        val fuelList = mutableListOf<FuelLog>()
        if (root.has("fuelLogs")) {
            val fuelArray = root.getJSONArray("fuelLogs")
            for (i in 0 until fuelArray.length()) {
                val fObj = fuelArray.getJSONObject(i)
                val fuel = FuelLog(
                    id = fObj.optLong("id", 0L),
                    vehicleId = fObj.optLong("vehicleId", 1L),
                    fillDate = fObj.optLong("fillDate", System.currentTimeMillis()),
                    odometerKm = fObj.optInt("odometerKm", 0),
                    fuelLiters = fObj.optDouble("fuelLiters", 0.0),
                    pricePerLiter = fObj.optDouble("pricePerLiter", 2.18),
                    totalCostSar = fObj.optDouble("totalCostSar", 0.0),
                    distanceDrivenKm = fObj.optInt("distanceDrivenKm", 0),
                    fuelType = fObj.optString("fuelType", "بنزين 91"),
                    isFullTank = fObj.optBoolean("isFullTank", true),
                    notes = fObj.optString("notes", "")
                )
                fuelList.add(fuel)
            }
        }

        return BackupData(vehicle = vehicle, partSchedules = parts, serviceLogs = logs, fuelLogs = fuelList)
    }

    fun exportAndShareBackup(
        context: Context,
        vehicle: Vehicle?,
        partSchedules: List<PartSchedule>,
        serviceLogs: List<ServiceLog>,
        fuelLogs: List<FuelLog> = emptyList()
    ) {
        try {
            val jsonContent = generateBackupJson(vehicle, partSchedules, serviceLogs, fuelLogs)
            val fileName = "patrol_backup_${System.currentTimeMillis()}.json"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { out ->
                out.write(jsonContent.toByteArray(Charsets.UTF_8))
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "تصدير ومشاركة النسخة الاحتياطية (JSON)")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "خطأ أثناء إنشاء النسخة الاحتياطية: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
