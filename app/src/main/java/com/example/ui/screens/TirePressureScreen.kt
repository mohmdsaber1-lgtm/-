package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TirePressureLog
import com.example.data.model.TireStatus
import com.example.data.model.Vehicle
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TirePressureScreen(
    tireLogs: List<TirePressureLog>,
    vehicle: Vehicle?,
    onAddLogClick: () -> Unit,
    onDeleteLogClick: (TirePressureLog) -> Unit
) {
    val latestLog = remember(tireLogs) { tireLogs.firstOrNull() }
    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar")) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner & Add Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                    .background(PatrolGold.copy(alpha = 0.2f)),
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
                                    text = "قياسات ضغط الإطارات",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "متابعة أداء وسلامة إطارات الباترول",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Button(
                            onClick = onAddLogClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_tire_log_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تسجيل قياس", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Vehicle Visual Diagram Card (Latest Log)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "مخطط الإطارات الأخير (باترول 2015)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        if (latestLog != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PatrolGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = latestLog.drivingContext,
                                    color = PatrolGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (latestLog != null) {
                        // Visual Car Frame Layout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PatrolDarkCharcoal)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Front Axle (FL, FR)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TireWheelDisplay(
                                        title = "الأمامي الأيسر FL",
                                        psi = latestLog.frontLeftPsi,
                                        status = latestLog.getStatusForPsi(latestLog.frontLeftPsi)
                                    )
                                    TireWheelDisplay(
                                        title = "الأمامي الأيمن FR",
                                        psi = latestLog.frontRightPsi,
                                        status = latestLog.getStatusForPsi(latestLog.frontRightPsi)
                                    )
                                }

                                // Center Car Body Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "NISSAN PATROL 4WD",
                                            color = Color.LightGray,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "متوسط الضغط: ${String.format(Locale.US, "%.1f", latestLog.averagePsi)} PSI",
                                            color = PatrolGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                // Rear Axle (RL, RR)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TireWheelDisplay(
                                        title = "الخلفي الأيسر RL",
                                        psi = latestLog.rearLeftPsi,
                                        status = latestLog.getStatusForPsi(latestLog.rearLeftPsi)
                                    )
                                    TireWheelDisplay(
                                        title = "الخلفي الأيمن RR",
                                        psi = latestLog.rearRightPsi,
                                        status = latestLog.getStatusForPsi(latestLog.rearRightPsi)
                                    )
                                }

                                // Spare Tire Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Gray.copy(alpha = 0.15f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TireRepair,
                                        contentDescription = "Spare",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "الاستبنة (الاحتياطي): ${latestLog.sparePsi} PSI",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "العداد: ${latestLog.odometerKm} كم",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "آخر قياس: ${dateFormatter.format(Date(latestLog.recordDate))}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = "لا توجد قياسات مسجلة بعد. اضغط على 'تسجيل قياس' لإضافة أول قراءة.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // Nissan Patrol Tire Pressure Recommendation Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Specs",
                            tint = PatrolGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دليل الضغط الموصى به لنيسان باترول:",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GuideCard(
                            title = "القيادة اليومية / المدينة",
                            psiText = "35 PSI",
                            barText = "(2.4 BAR)",
                            bgColor = PatrolDarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )
                        GuideCard(
                            title = "السفر والخطوط السريعة",
                            psiText = "36 PSI",
                            barText = "(2.5 BAR)",
                            bgColor = PatrolDarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )
                        GuideCard(
                            title = "الرمال والطعوس",
                            psiText = "15-18 PSI",
                            barText = "(1.1 BAR)",
                            bgColor = PatrolDarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // History Section Header
        item {
            Text(
                text = "سجل القياسات السابقة (${tireLogs.size}):",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // History Items
        items(tireLogs, key = { it.id }) { log ->
            TireLogCardItem(
                log = log,
                dateFormatter = dateFormatter,
                onDelete = { onDeleteLogClick(log) }
            )
        }
    }
}

@Composable
private fun TireWheelDisplay(
    title: String,
    psi: Double,
    status: TireStatus
) {
    val (statusBg, statusBorder, statusText) = when (status) {
        TireStatus.OPTIMAL -> Triple(PatrolEmerald.copy(alpha = 0.15f), PatrolEmerald, "سليم")
        TireStatus.OFFROAD_LOW -> Triple(PatrolAmber.copy(alpha = 0.15f), PatrolAmber, "تنفيس رمل")
        TireStatus.LOW -> Triple(PatrolAmber.copy(alpha = 0.15f), PatrolAmber, "منخفض")
        TireStatus.HIGH -> Triple(PatrolCrimson.copy(alpha = 0.15f), PatrolCrimson, "مرتفع")
    }

    Box(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(statusBg)
            .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$psi PSI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusBorder
            )
        }
    }
}

@Composable
private fun GuideCard(
    title: String,
    psiText: String,
    barText: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = psiText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PatrolGold
            )
            Text(
                text = barText,
                fontSize = 9.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun TireLogCardItem(
    log: TirePressureLog,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PatrolGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = log.drivingContext,
                            color = PatrolGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${log.odometerKm} كم",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormatter.format(Date(log.recordDate)),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4 Tires Readings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("FL: ${log.frontLeftPsi} PSI", fontSize = 11.sp, color = Color.LightGray)
                Text("FR: ${log.frontRightPsi} PSI", fontSize = 11.sp, color = Color.LightGray)
                Text("RL: ${log.rearLeftPsi} PSI", fontSize = 11.sp, color = Color.LightGray)
                Text("RR: ${log.rearRightPsi} PSI", fontSize = 11.sp, color = Color.LightGray)
                Text("الموقع: ${log.sparePsi} PSI", fontSize = 11.sp, color = PatrolGold)
            }

            if (log.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظة: ${log.notes}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
