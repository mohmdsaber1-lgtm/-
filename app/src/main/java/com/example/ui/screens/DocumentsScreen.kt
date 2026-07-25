package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import com.example.utils.BackupData
import com.example.utils.DatabaseBackupUtils
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentsScreen(
    vehicle: Vehicle?,
    onOpenEditDocDialog: (docName: String, currentDaysLeft: Long, docType: String) -> Unit,
    onTestNotification: (Context) -> Unit,
    onTestFahsNotification: (Context) -> Unit,
    onTestIstimaraNotification: (Context) -> Unit,
    onExportBackup: (Context) -> Unit = {},
    onRestoreBackup: (BackupData) -> Unit = {}
) {
    val context = LocalContext.current

    var pendingRestoreData by remember { mutableStateOf<BackupData?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val jsonString = inputStream?.bufferedReader()?.use { it.readText() }
                if (!jsonString.isNullOrBlank()) {
                    val parsedData = DatabaseBackupUtils.parseBackupJson(jsonString)
                    pendingRestoreData = parsedData
                    showRestoreConfirmDialog = true
                } else {
                    Toast.makeText(context, "الملف المحدد فارغ", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "خطأ في قراءة ملف JSON: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    if (vehicle == null) return

    val now = System.currentTimeMillis()
    val istimaraDays = (vehicle.istimaraExpiryDate - now) / (24 * 60 * 60 * 1000)
    val fahsDays = (vehicle.fahsExpiryDate - now) / (24 * 60 * 60 * 1000)
    val insuranceDays = (vehicle.insuranceExpiryDate - now) / (24 * 60 * 60 * 1000)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "الاستمارة والفحص الدوري لباترول 2015",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "متابعة صلاحية وثائق السيارة وإرسال التنبيهات التلقائية قبل الانتهاء",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // 1. Istimara Card (رخصة سير المركبة)
        item {
            DocumentCard(
                title = "استمارة المركبة (رخصة السير)",
                daysLeft = istimaraDays,
                expiryDateMillis = vehicle.istimaraExpiryDate,
                icon = Icons.Default.Assignment,
                onEditDate = {
                    onOpenEditDocDialog("استمارة المركبة", istimaraDays, "ISTIMARA")
                },
                onTestNotice = {
                    onTestIstimaraNotification(context)
                }
            )
        }

        // 2. Fahs Card (الفحص الدوري الفني)
        item {
            DocumentCard(
                title = "الفحص الدوري الفني للمركبة",
                daysLeft = fahsDays,
                expiryDateMillis = vehicle.fahsExpiryDate,
                icon = Icons.Default.VerifiedUser,
                onEditDate = {
                    onOpenEditDocDialog("الفحص الدوري", fahsDays, "FAHS")
                },
                onTestNotice = {
                    onTestFahsNotification(context)
                }
            )
        }

        // 3. Insurance Card (تأمين المركبة)
        item {
            DocumentCard(
                title = "تأمين المركبة",
                daysLeft = insuranceDays,
                expiryDateMillis = vehicle.insuranceExpiryDate,
                icon = Icons.Default.Security,
                onEditDate = {
                    onOpenEditDocDialog("تأمين المركبة", insuranceDays, "INSURANCE")
                },
                onTestNotice = {
                    onTestNotification(context)
                }
            )
        }

        // 4. Test Notification Control Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notification_control_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PatrolGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notification",
                                tint = PatrolGold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "نظام التنبيهات التلقائية المستمرة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "يتم إرسال إشعار تلقائي قبل 30 يوماً و15 يوماً من الموعد",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onTestNotification(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_push_notification_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = "Test")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إرسال إشعار تجريبي فوري للجوال")
                    }
                }
            }
        }

        // 5. Backup & Restore Database Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("backup_restore_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PatrolEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = "Backup",
                                tint = PatrolEmerald
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "النسخ الاحتياطي واستعادة البيانات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "حفظ واستعادة سجلات الصيانة وجدول القطع بتنسيق JSON لنقلها بين الأجهزة",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onExportBackup(context) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_backup_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PatrolEmerald),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Export Backup",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تصدير النسخة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { restoreLauncher.launch("application/json") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_backup_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PatrolGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Restore Backup",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("استعادة النسخة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Data Restore
    if (showRestoreConfirmDialog && pendingRestoreData != null) {
        val data = pendingRestoreData!!
        AlertDialog(
            onDismissRequest = {
                showRestoreConfirmDialog = false
                pendingRestoreData = null
            },
            title = {
                Text(
                    text = "تأكيد استعادة النسخة الاحتياطية",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "تحذير: سيتم استبدال جميع سجلات الصيانة وجداول القطع الحالية بهذه النسخة.",
                        color = PatrolCrimson,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "تفاصيل النسخة المراد استعادتها:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• المركبة: ${data.vehicle?.name ?: "غير محدد"}", color = Color.LightGray)
                    Text(text = "• عدد قطع جدول الصيانة: ${data.partSchedules.size}", color = Color.LightGray)
                    Text(text = "• عدد سجلات الإصلاح والصيانة: ${data.serviceLogs.size}", color = Color.LightGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRestoreBackup(data)
                        showRestoreConfirmDialog = false
                        pendingRestoreData = null
                        Toast.makeText(context, "تمت استعادة البيانات بنجاح!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PatrolEmerald)
                ) {
                    Text("استعادة الآن")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRestoreConfirmDialog = false
                        pendingRestoreData = null
                    }
                ) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = PatrolCardDark
        )
    }
}

@Composable
fun DocumentCard(
    title: String,
    daysLeft: Long,
    expiryDateMillis: Long,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onEditDate: () -> Unit,
    onTestNotice: () -> Unit
) {
    val dateString = DateFormat.getDateInstance(DateFormat.LONG, Locale("ar")).format(Date(expiryDateMillis))

    val (statusLabel, badgeColor) = when {
        daysLeft <= 0 -> "منتهي - يتطلب التجديد الآن!" to PatrolCrimson
        daysLeft <= 30 -> "قريب الانتهاء ($daysLeft يوم)" to PatrolAmber
        else -> "ساري المفعول ($daysLeft يوم متبقي)" to PatrolEmerald
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doc_card_${title.hashCode()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = title, tint = PatrolGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "تاريخ الانتهاء الموثق: $dateString",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditDate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل الموعد", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onTestNotice,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("اختبار التنبيه", color = PatrolGold, fontSize = 12.sp)
                }
            }
        }
    }
}
