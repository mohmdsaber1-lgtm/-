package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ServiceLog
import com.example.data.model.Vehicle
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    fun exportToCsv(context: Context, vehicle: Vehicle?, serviceLogs: List<ServiceLog>) {
        try {
            val fileName = "patrol_maintenance_history_${System.currentTimeMillis()}.csv"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, fileName)

            val csvContent = StringBuilder()
            // UTF-8 BOM for Excel Arabic support
            csvContent.append("\uFEFF")

            // Headers
            csvContent.append("اسم القطعة/الصيانة,التصنيف,قراءة العداد (كم),التكلفة (ريال سعودي),التاريخ,الورشة/المركز,ملاحظات\n")

            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("ar"))

            serviceLogs.forEach { log ->
                val dateStr = dateFormat.format(Date(log.serviceDate))
                val cleanPartName = escapeCsvField(log.partName)
                val cleanCategory = escapeCsvField(log.category)
                val cleanWorkshop = escapeCsvField(log.workshopName)
                val cleanNotes = escapeCsvField(log.notes)

                csvContent.append("$cleanPartName,$cleanCategory,${log.odometerKm},${log.costSar},$dateStr,$cleanWorkshop,$cleanNotes\n")
            }

            FileOutputStream(file).use { out ->
                out.write(csvContent.toString().toByteArray(Charsets.UTF_8))
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            shareFile(context, uri, "text/csv", "مشاركة سجل الصيانة (CSV)")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "حدث خطأ أثناء تصدير ملف CSV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun exportToPdf(context: Context, vehicle: Vehicle?, serviceLogs: List<ServiceLog>) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            var pageNumber = 1

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }

            val subtitlePaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.RIGHT
            }

            val headerPaint = Paint().apply {
                color = Color.WHITE
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }

            val bgHeaderPaint = Paint().apply {
                color = Color.rgb(184, 134, 11) // Patrol Gold / Dark Amber
            }

            val bodyPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.RIGHT
            }

            val linePaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
            }

            var y = 50f

            // Title Header
            val vehicleName = vehicle?.name ?: "نيسان باترول 2015 V8 VK56"
            canvas.drawText("تقرير سجلات الصيانة والإصلاحات - $vehicleName", 565f, y, titlePaint)
            y += 24f

            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("ar"))
            val todayStr = dateFormat.format(Date())
            val totalCost = serviceLogs.sumOf { it.costSar }
            val formattedTotalCost = NumberFormat.getNumberInstance(Locale.US).format(totalCost)

            canvas.drawText("تاريخ التقرير: $todayStr | إجمالي المصاريف: $formattedTotalCost ريال سعودي | عدد العمليات: ${serviceLogs.size}", 565f, y, subtitlePaint)
            y += 30f

            // Table Header Background
            canvas.drawRect(30f, y, 565f, y + 26f, bgHeaderPaint)
            y += 18f

            // Table Columns (X coords from right to left for Arabic)
            // 565 -> 440: اسم القطعة
            // 440 -> 350: التصنيف
            // 350 -> 280: العداد (كم)
            // 280 -> 210: التكلفة (ر.س)
            // 210 -> 130: التاريخ
            // 130 -> 30: الورشة/المركز
            canvas.drawText("اسم القطعة / الصيانة", 555f, y, headerPaint)
            canvas.drawText("التصنيف", 430f, y, headerPaint)
            canvas.drawText("العداد (كم)", 340f, y, headerPaint)
            canvas.drawText("التكلفة (ر.س)", 270f, y, headerPaint)
            canvas.drawText("التاريخ", 200f, y, headerPaint)
            canvas.drawText("الورشة/المركز", 120f, y, headerPaint)

            y += 18f

            // Table Rows
            serviceLogs.forEach { log ->
                // Check if page overflow
                if (y > pageHeight - 60) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 50f

                    // Repeat Header on new page
                    canvas.drawRect(30f, y, 565f, y + 26f, bgHeaderPaint)
                    y += 18f
                    canvas.drawText("اسم القطعة / الصيانة", 555f, y, headerPaint)
                    canvas.drawText("التصنيف", 430f, y, headerPaint)
                    canvas.drawText("العداد (كم)", 340f, y, headerPaint)
                    canvas.drawText("التكلفة (ر.س)", 270f, y, headerPaint)
                    canvas.drawText("التاريخ", 200f, y, headerPaint)
                    canvas.drawText("الورشة/المركز", 120f, y, headerPaint)
                    y += 18f
                }

                val logDate = dateFormat.format(Date(log.serviceDate))
                val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(log.costSar)

                canvas.drawText(truncateText(log.partName, 18), 555f, y, bodyPaint)
                canvas.drawText(truncateText(log.category, 12), 430f, y, bodyPaint)
                canvas.drawText("${log.odometerKm}", 340f, y, bodyPaint)
                canvas.drawText(formattedCost, 270f, y, bodyPaint)
                canvas.drawText(logDate, 200f, y, bodyPaint)
                canvas.drawText(truncateText(log.workshopName, 14), 120f, y, bodyPaint)

                y += 6f
                canvas.drawLine(30f, y, 565f, y, linePaint)
                y += 18f
            }

            pdfDocument.finishPage(page)

            // Save PDF File
            val fileName = "patrol_maintenance_report_${System.currentTimeMillis()}.pdf"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            shareFile(context, uri, "application/pdf", "مشاركة تقرير الصيانة (PDF)")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "حدث خطأ أثناء تصدير ملف PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareFile(context: Context, uri: Uri, mimeType: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }

    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength - 1) + "…"
        } else {
            text
        }
    }
}
