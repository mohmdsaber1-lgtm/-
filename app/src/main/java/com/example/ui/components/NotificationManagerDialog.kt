package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.MaintenanceStatus
import com.example.data.model.PartSchedule
import com.example.data.model.Vehicle
import com.example.notification.NotificationHelper
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold

@Composable
fun NotificationManagerDialog(
    vehicle: Vehicle?,
    partSchedules: List<PartSchedule>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedKmThreshold by remember { mutableIntStateOf(1000) }
    var selectedDaysThreshold by remember { mutableIntStateOf(15) }

    val currentKm = vehicle?.currentOdometer ?: 185000

    val dueOrSoonParts = remember(partSchedules, currentKm, selectedKmThreshold, selectedDaysThreshold) {
        partSchedules.filter { part ->
            val remKm = part.getRemainingKm(currentKm)
            val remDays = part.getRemainingDays()
            remKm <= selectedKmThreshold || remDays <= selectedDaysThreshold
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PatrolCardDark,
        shape = RoundedCornerShape(20.dp),
        title = {
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
                        contentDescription = "Notification Settings",
                        tint = PatrolGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "نظام تنبيهات صيانة باترول 2015",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "تذكير تلقائي بالمسافة والأيام",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Summary Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PatrolDarkCharcoal)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (dueOrSoonParts.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = "Status",
                            tint = if (dueOrSoonParts.isNotEmpty()) PatrolAmber else PatrolEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (dueOrSoonParts.isNotEmpty())
                                    "يوجد ${dueOrSoonParts.size} قطعة تتطلب تنبيه صيانة مستحق"
                                else
                                    "جميع قطع صيانة الباترول في حالة ممتازة!",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "عداد المركبة الحالي: $currentKm كم",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Threshold 1: Distance Trigger Selector
                Column {
                    Text(
                        text = "عتبة التنبيه قبل المسافة (كم):",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2000, 1000, 500, 0).forEach { kmVal ->
                            val label = if (kmVal == 0) "عند الموعد" else "$kmVal كم"
                            val isSelected = selectedKmThreshold == kmVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PatrolGold else Color.Gray.copy(alpha = 0.2f))
                                    .clickable { selectedKmThreshold = kmVal }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Threshold 2: Date Trigger Selector
                Column {
                    Text(
                        text = "عتبة التنبيه قبل الموعد الزمني (أيام):",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(30, 15, 7, 0).forEach { daysVal ->
                            val label = if (daysVal == 0) "عند الموعد" else "$daysVal يوم"
                            val isSelected = selectedDaysThreshold == daysVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PatrolGold else Color.Gray.copy(alpha = 0.2f))
                                    .clickable { selectedDaysThreshold = daysVal }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Instant Action Buttons
                Button(
                    onClick = {
                        val count = NotificationHelper.checkAndSendAllDueNotifications(
                            context = context,
                            vehicle = vehicle,
                            partSchedules = partSchedules,
                            kmThreshold = selectedKmThreshold,
                            daysThreshold = selectedDaysThreshold
                        )
                        Toast.makeText(
                            context,
                            "تم فحص الصيانة وإرسال $count إشعار للنظام بنجاح 🔔",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("check_now_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Check Now",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "فحص وتفعيل التنبيهات المستحقة الآن",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        NotificationHelper.sendTestNotification(context)
                        Toast.makeText(context, "تم إرسال إشعار تجريبي لنظام الأندرويد 📱", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إرسال إشعار تجريبي لنظام الأندروide", color = PatrolGold, fontSize = 12.sp)
                }

                // List of items status
                Text(
                    text = "حالة تنبيهات قطع نيسان باترول (${partSchedules.size}):",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )

                partSchedules.forEach { part ->
                    val remKm = part.getRemainingKm(currentKm)
                    val remDays = part.getRemainingDays()
                    val status = part.getStatus(currentKm)

                    val isAlerting = remKm <= selectedKmThreshold || remDays <= selectedDaysThreshold

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PatrolDarkCharcoal)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = part.partNameAr,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "متبقي $remKm كم | $remDays يوم",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        val (statusText, statusColor) = when {
                            status == MaintenanceStatus.DUE_NOW -> "مستحق الآن" to PatrolCrimson
                            isAlerting -> "تنبيه قريب" to PatrolAmber
                            else -> "طبيعي" to PatrolEmerald
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold)
            ) {
                Text("إغلاق", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
