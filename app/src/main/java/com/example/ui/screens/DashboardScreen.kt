package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceStatus
import androidx.compose.material.icons.filled.TireRepair
import com.example.data.model.PartSchedule
import com.example.data.model.TirePressureLog
import com.example.ui.DashboardSummary
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    summary: DashboardSummary?,
    latestTireLog: TirePressureLog? = null,
    onOpenOdometerDialog: () -> Unit,
    onOpenLogServiceDialog: (PartSchedule?) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToTires: () -> Unit,
    onOpenNotificationDialog: () -> Unit
) {
    if (summary == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("جاري تحميل بيانات نيسان باترول...")
        }
        return
    }

    val vehicle = summary.vehicle
    val formattedOdometer = NumberFormat.getNumberInstance(Locale.US).format(vehicle.currentOdometer)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Nissan Patrol Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vehicle_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E293B),
                                    PatrolDarkCharcoal
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PatrolGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Patrol",
                                        tint = PatrolGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = vehicle.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${vehicle.engine} | ${vehicle.trim}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            // License Plate Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = vehicle.plateNumber,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Odometer Box with Update Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Odometer",
                                    tint = PatrolGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "العداد الحالي للمركبة",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$formattedOdometer كم",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenOdometerDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("update_odometer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Odometer",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تحديث العداد", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 1.5 Smart Maintenance Notification System Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenNotificationDialog() }
                    .testTag("notifications_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PatrolGold.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications System",
                                tint = PatrolGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "نظام التنبيهات الذكي للباترول",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "تذكير بالمسافة والأيام قبل حلول موعد الصيانة",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Button(
                        onClick = onOpenNotificationDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_notifications_manager")
                    ) {
                        Text("إدارة التنبيهات", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 1.8 Tire Pressure Quick Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTires() }
                    .testTag("dashboard_tires_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PatrolGold.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TireRepair,
                                    contentDescription = "Tires",
                                    tint = PatrolGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ضغط الإطارات (باترول 2015)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (latestTireLog != null)
                                        "آخر قياس: ${latestTireLog.drivingContext} (${latestTireLog.odometerKm} كم)"
                                    else
                                        "متابعة سلامة وضغط إطارات 4WD",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Text(
                            text = "عرض الكل >",
                            fontSize = 11.sp,
                            color = PatrolGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (latestTireLog != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PatrolDarkCharcoal)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الأمامية", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    "${latestTireLog.frontLeftPsi} / ${latestTireLog.frontRightPsi} PSI",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Column {
                                Text("الخلفية", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    "${latestTireLog.rearLeftPsi} / ${latestTireLog.rearRightPsi} PSI",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Column {
                                Text("الاستبنة", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    "${latestTireLog.sparePsi} PSI",
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolGold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Health & Status Summary Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Due Now Box
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = "مستحق الآن",
                    count = summary.dueNowCount.toString(),
                    color = if (summary.dueNowCount > 0) PatrolCrimson else PatrolEmerald,
                    icon = if (summary.dueNowCount > 0) Icons.Default.Warning else Icons.Default.Verified
                )

                // Due Soon Box
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = "قريباً",
                    count = summary.dueSoonCount.toString(),
                    color = PatrolAmber,
                    icon = Icons.Default.Info
                )

                // Good Box
                StatusCard(
                    modifier = Modifier.weight(1f),
                    title = "حالة ممتازة",
                    count = summary.goodCount.toString(),
                    color = PatrolEmerald,
                    icon = Icons.Default.Verified
                )
            }
        }

        // 3. Istimara & Fahs Expiration Reminder Cards
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDocuments() }
                    .testTag("documents_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تنبيهات الوثائق الرسمية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "عرض التفاصيل >",
                            style = MaterialTheme.typography.bodySmall,
                            color = PatrolGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Istimara Badge
                        DocStatusChip(
                            modifier = Modifier.weight(1f),
                            title = "الاستمارة",
                            daysLeft = summary.istimaraDaysLeft
                        )

                        // Fahs Badge
                        DocStatusChip(
                            modifier = Modifier.weight(1f),
                            title = "الفحص الدوري",
                            daysLeft = summary.fahsDaysLeft
                        )
                    }
                }
            }
        }

        // 4. Action Buttons Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onOpenLogServiceDialog(null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_log_service_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تسجيل صيانة", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToSchedule,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = "Schedule", tint = PatrolGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("جدول الوكالة", color = PatrolGold)
                }
            }
        }

        // 5. Urgent Maintenance Items Banner & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قطع وصيانات تتطلب الاهتمام (${summary.urgentAlerts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (summary.urgentAlerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "All good",
                            tint = PatrolEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "جميع قطع نيسان باترول بحالة ممتازة!",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "لا يوجد أي قطع مستحقة الصيانة في الوقت الحالي.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(summary.urgentAlerts) { part ->
                PartAlertCard(
                    part = part,
                    currentKm = vehicle.currentOdometer,
                    onLogService = { onOpenLogServiceDialog(part) }
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun DocStatusChip(
    modifier: Modifier = Modifier,
    title: String,
    daysLeft: Long
) {
    val (statusText, badgeColor) = when {
        daysLeft <= 0 -> "منتهي!" to PatrolCrimson
        daysLeft <= 30 -> "متبقي $daysLeft يوم" to PatrolAmber
        else -> "متبقي $daysLeft يوم" to PatrolEmerald
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = statusText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = badgeColor)
        }
    }
}

@Composable
fun PartAlertCard(
    part: PartSchedule,
    currentKm: Int,
    onLogService: () -> Unit
) {
    val remainingKm = part.getRemainingKm(currentKm)
    val status = part.getStatus(currentKm)

    val (badgeText, badgeColor) = when (status) {
        MaintenanceStatus.DUE_NOW -> "مستحق الآن" to PatrolCrimson
        MaintenanceStatus.DUE_SOON -> "قريباً جدًا" to PatrolAmber
        MaintenanceStatus.GOOD -> "ساري" to PatrolEmerald
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("part_alert_card_${part.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = badgeText, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = part.category, color = Color.Gray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = part.partNameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = if (remainingKm <= 0) "تجاوزت الموعد بـ ${-remainingKm} كم" else "متبقي $remainingKm كم على الموعد",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (remainingKm <= 0) PatrolCrimson else Color.LightGray
                )
            }

            Button(
                onClick = onLogService,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("log_service_button_${part.id}")
            ) {
                Text("تسجيل صيانة", fontSize = 12.sp)
            }
        }
    }
}
