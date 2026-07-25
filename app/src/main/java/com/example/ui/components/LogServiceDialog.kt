package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PartSchedule
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.io.File
import java.io.FileOutputStream

@Composable
fun LogServiceDialog(
    partSchedule: PartSchedule?,
    currentVehicleKm: Int,
    onDismiss: () -> Unit,
    onConfirm: (
        part: PartSchedule?,
        partName: String,
        category: String,
        serviceKm: Int,
        serviceDate: Long,
        costSar: Double,
        workshop: String,
        notes: String,
        imageUri: String?
    ) -> Unit
) {
    val context = LocalContext.current

    var partName by remember { mutableStateOf(partSchedule?.partNameAr ?: "") }
    var category by remember { mutableStateOf(partSchedule?.category ?: "زيوت وسوائل") }
    var kmInput by remember { mutableStateOf(currentVehicleKm.toString()) }
    var costInput by remember { mutableStateOf("0") }
    var workshop by remember { mutableStateOf("مركز صيانة الباترول") }
    var notes by remember { mutableStateOf(partSchedule?.specification ?: "") }

    var attachedImagePath by remember { mutableStateOf<String?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isError by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            previewBitmap = bitmap
            val savedPath = saveBitmapToLocalFile(context, bitmap)
            attachedImagePath = savedPath
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = copyUriToLocalFile(context, uri)
            attachedImagePath = savedPath
            previewBitmap = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (partSchedule != null) "تسجيل صيانة: ${partSchedule.partNameAr}" else "تسجيل إصلاح/صيانة جديد",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                if (partSchedule == null) {
                    OutlinedTextField(
                        value = partName,
                        onValueChange = { partName = it },
                        label = { Text("اسم القطعة / الصيانة") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("service_part_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("التصنيف (مثال: زيوت, فرامل, كهرباء)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = kmInput,
                    onValueChange = { kmInput = it.filter { char -> char.isDigit() } },
                    label = { Text("قراءة العداد عند الصيانة (كم)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_km_input")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = costInput,
                    onValueChange = { costInput = it },
                    label = { Text("التكلفة الإجمالية (ريال سعودي SAR)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_cost_input")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = workshop,
                    onValueChange = { workshop = it },
                    label = { Text("اسم الورشة / المركز / الوكالة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات / رقم الفاتورة / الضمان") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Invoice & Parts Photo Attachment
                Text(
                    text = "صورة الفاتورة أو القطعة (اختياري)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PatrolGold
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (attachedImagePath != null || previewBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, PatrolGold, RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        if (previewBitmap != null) {
                            Image(
                                bitmap = previewBitmap!!.asImageBitmap(),
                                contentDescription = "Captured Part Photo",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (attachedImagePath != null) {
                            AsyncImage(
                                model = File(attachedImagePath!!),
                                contentDescription = "Invoice Photo",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Remove Photo Button
                        IconButton(
                            onClick = {
                                attachedImagePath = null
                                previewBitmap = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = PatrolCrimson
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("capture_camera_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PatrolGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الكاميرا", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_gallery_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PatrolEmerald)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("المعرض", fontSize = 12.sp)
                        }
                    }
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يرجى التأكد من ملء الحقول ورقم الكيلومترات الصحيح",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = kmInput.toIntOrNull()
                    val cost = costInput.toDoubleOrNull() ?: 0.0
                    val name = if (partSchedule != null) partSchedule.partNameAr else partName

                    if (name.isNotBlank() && km != null && km > 0) {
                        onConfirm(
                            partSchedule,
                            name,
                            category,
                            km,
                            System.currentTimeMillis(),
                            cost,
                            workshop,
                            notes,
                            attachedImagePath
                        )
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("save_service_log_button")
            ) {
                Text("تأكيد وحفظ الصيانة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun saveBitmapToLocalFile(context: Context, bitmap: Bitmap): String? {
    return try {
        val photosDir = File(context.filesDir, "service_photos")
        if (!photosDir.exists()) photosDir.mkdirs()
        val file = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun copyUriToLocalFile(context: Context, sourceUri: Uri): String? {
    return try {
        val photosDir = File(context.filesDir, "service_photos")
        if (!photosDir.exists()) photosDir.mkdirs()
        val file = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
