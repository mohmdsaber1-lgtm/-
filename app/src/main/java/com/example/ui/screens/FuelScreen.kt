package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelLog
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FuelChartMetric {
    CONSUMPTION_L100KM,
    COST_SAR
}

@Composable
fun FuelScreen(
    fuelLogs: List<FuelLog>,
    currentOdometer: Int,
    onAddFuelClick: () -> Unit,
    onDeleteFuelLog: (FuelLog) -> Unit
) {
    // Sort fuel logs chronologically for charts (oldest to newest)
    val chronologicalLogs = remember(fuelLogs) {
        fuelLogs.sortedBy { it.fillDate }
    }

    // Calculations
    val validLogsWithDist = remember(fuelLogs) {
        fuelLogs.filter { it.distanceDrivenKm > 0 && it.fuelLiters > 0 }
    }

    val totalLiters = remember(fuelLogs) { fuelLogs.sumOf { it.fuelLiters } }
    val totalCost = remember(fuelLogs) { fuelLogs.sumOf { it.totalCostSar } }
    val totalDistance = remember(validLogsWithDist) { validLogsWithDist.sumOf { it.distanceDrivenKm } }

    val avgLitersPer100Km = remember(totalLiters, totalDistance) {
        if (totalDistance > 0 && totalLiters > 0) (totalLiters / totalDistance) * 100 else 0.0
    }

    val avgKmPerLiter = remember(totalLiters, totalDistance) {
        if (totalLiters > 0 && totalDistance > 0) totalDistance / totalLiters else 0.0
    }

    val avgCostPerKm = remember(totalCost, totalDistance) {
        if (totalDistance > 0 && totalCost > 0) totalCost / totalDistance else 0.0
    }

    val formattedTotalCost = NumberFormat.getNumberInstance(Locale.US).format(totalCost.toInt())

    var chartMetric by remember { mutableStateOf(FuelChartMetric.CONSUMPTION_L100KM) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header & Add Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "معدل استهلاك وقود الباترول",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "حساب معدل الاستهلاك والتكلفة بوقود 91 / 95",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = onAddFuelClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_fuel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تعبئة جديدة", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Hero KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1: Average Consumption KPI (L/100km & km/L)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_kpi_consumption_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PatrolEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = "Fuel Consumption",
                                tint = PatrolEmerald,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "متوسط الاستهلاك العام",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (avgLitersPer100Km > 0) String.format(Locale.US, "%.1f", avgLitersPer100Km) else "--",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolEmerald
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "لتر / 100 كم",
                                    fontSize = 13.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (avgKmPerLiter > 0) String.format(Locale.US, "يقطع %.2f كم لكل لتر", avgKmPerLiter) else "لا توجد بيانات كافية",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PatrolGold
                                )

                                // V8 Rating Badge
                                val (badgeText, badgeColor) = when {
                                    avgLitersPer100Km == 0.0 -> "" to Color.Transparent
                                    avgLitersPer100Km < 14.5 -> "ممتاز للـ V8" to PatrolEmerald
                                    avgLitersPer100Km <= 17.5 -> "طبيعي للباترول" to PatrolAmber
                                    else -> "استهلاك مرتفع" to PatrolCrimson
                                }

                                if (badgeText.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(badgeColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            color = badgeColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Row with 2 Secondary KPIs: Total Spent & Cost/Km
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // KPI 2: Total Spent
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Cost",
                                    tint = PatrolGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "إجمالي مصاريف الوقود",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$formattedTotalCost ر.س",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PatrolGold
                            )
                            Text(
                                text = "مجمّع ${fuelLogs.size} تعبئات (${String.format(Locale.US, "%.0f", totalLiters)} لتر)",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // KPI 3: Cost per Km
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Cost per Km",
                                    tint = PatrolAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تكلفة الكيلومتر",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (avgCostPerKm > 0) String.format(Locale.US, "%.2f ر.س / كم", avgCostPerKm) else "--",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "إجمالي المسافة: $totalDistance كم",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Fuel Consumption & Expense Trend Interactive Canvas Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fuel_chart_card"),
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
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Trend Chart",
                                tint = PatrolEmerald
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "رسم بياني لتطور استهلاك الوقود",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Toggle Metric (L/100km vs SAR)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (chartMetric == FuelChartMetric.CONSUMPTION_L100KM) PatrolEmerald else Color.Transparent)
                                    .clickable {
                                        chartMetric = FuelChartMetric.CONSUMPTION_L100KM
                                        selectedPointIndex = null
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "لتر/100كم",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartMetric == FuelChartMetric.CONSUMPTION_L100KM) Color.White else Color.Gray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (chartMetric == FuelChartMetric.COST_SAR) PatrolEmerald else Color.Transparent)
                                    .clickable {
                                        chartMetric = FuelChartMetric.COST_SAR
                                        selectedPointIndex = null
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "التكلفة (ر.س)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartMetric == FuelChartMetric.COST_SAR) Color.White else Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (chronologicalLogs.isEmpty()) {
                        Text(
                            text = "قم بتسجيل أول تعبئة وقود لعرض الرسم البياني التفاعلي.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else if (chartMetric == FuelChartMetric.CONSUMPTION_L100KM) {
                        FuelConsumptionLineChart(
                            logs = chronologicalLogs,
                            avgL100 = avgLitersPer100Km,
                            selectedIndex = selectedPointIndex,
                            onPointSelected = { idx -> selectedPointIndex = idx }
                        )
                    } else {
                        FuelCostBarChart(
                            logs = chronologicalLogs,
                            selectedIndex = selectedPointIndex,
                            onBarSelected = { idx -> selectedPointIndex = idx }
                        )
                    }

                    // Inspection details box for selected point
                    selectedPointIndex?.let { idx ->
                        val log = chronologicalLogs.getOrNull(idx)
                        if (log != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))
                            val dateStr = dateFormat.format(Date(log.fillDate))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PatrolEmerald.copy(alpha = 0.15f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "تعبئة بتاريخ $dateStr (${log.odometerKm} كم)",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${log.totalCostSar} ريال",
                                            fontWeight = FontWeight.Bold,
                                            color = PatrolGold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "الكمية: ${log.fuelLiters} لتر (${log.fuelType})",
                                            fontSize = 12.sp,
                                            color = Color.LightGray
                                        )
                                        if (log.litersPer100Km > 0) {
                                            Text(
                                                text = String.format(Locale.US, "الاستهلاك: %.1f لتر/100كم", log.litersPer100Km),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PatrolEmerald
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Patrol Engine Tip Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Nissan Engine Tip",
                        tint = PatrolGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نصيحة نيسان باترول V8 (سعة 5.6 لتر):",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تعبئة بنزين 91 أو 95 موصى به رسمياً. للحفاظ على أقل استهلاك: نظّف حساس الهواء MAF وفلتر البنزين دورياً وتأكد من ضغط الإطارات (35 PSI).",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Refuel Logs History Header
        item {
            Text(
                text = "سجل التعبئات السابقة (${fuelLogs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Logs items
        if (fuelLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد تعبئات وقود مسجلة حتى الآن. اضغط \"تعبئة جديدة\" للبدء.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(fuelLogs, key = { it.id }) { log ->
                FuelLogCard(
                    log = log,
                    onDelete = { onDeleteFuelLog(log) }
                )
            }
        }
    }
}

@Composable
fun FuelConsumptionLineChart(
    logs: List<FuelLog>,
    avgL100: Double,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit
) {
    val validLogs = logs.filter { it.litersPer100Km > 0 }
    if (validLogs.isEmpty()) {
        Text("بيانات الاستهلاك تظهر بعد تسجيل مسافة مقطوعة وتعبئتين متتاليتين.", color = Color.Gray, fontSize = 12.sp)
        return
    }

    val maxL100 = (validLogs.maxOfOrNull { it.litersPer100Km } ?: 20.0).coerceAtLeast(18.0)
    val minL100 = (validLogs.minOfOrNull { it.litersPer100Km } ?: 10.0).coerceAtMost(10.0)

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .pointerInput(validLogs) {
                    detectTapGestures { offset ->
                        val sectionWidth = size.width / validLogs.size
                        val idx = (offset.x / sectionWidth).toInt().coerceIn(validLogs.indices)
                        onPointSelected(idx)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val count = validLogs.size
            val sectionWidth = width / count
            val paddingTop = 20.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val usableHeight = height - paddingTop - paddingBottom

            // Grid lines
            val gridCount = 3
            for (i in 0..gridCount) {
                val y = paddingTop + usableHeight * (i.toFloat() / gridCount)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Average benchmark line
            if (avgL100 > 0) {
                val fraction = ((avgL100 - minL100) / (maxL100 - minL100)).toFloat().coerceIn(0f, 1f)
                val avgY = paddingTop + usableHeight * (1f - fraction)
                drawLine(
                    color = PatrolGold.copy(alpha = 0.6f),
                    start = Offset(0f, avgY),
                    end = Offset(width, avgY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            }

            // Calculate points
            val points = validLogs.mapIndexed { i, log ->
                val x = i * sectionWidth + sectionWidth / 2
                val fraction = ((log.litersPer100Km - minL100) / (maxL100 - minL100)).toFloat().coerceIn(0f, 1f)
                val y = paddingTop + usableHeight * (1f - fraction)
                Offset(x, y)
            }

            // Path line & Fill gradient
            if (points.isNotEmpty()) {
                val linePath = Path()
                val fillPath = Path()

                linePath.moveTo(points[0].x, points[0].y)
                fillPath.moveTo(points[0].x, height - paddingBottom)
                fillPath.lineTo(points[0].x, points[0].y)

                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val controlX1 = p1.x + (p2.x - p1.x) / 2
                    val controlY1 = p1.y
                    val controlX2 = p1.x + (p2.x - p1.x) / 2
                    val controlY2 = p2.y

                    linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                }

                fillPath.lineTo(points.last().x, height - paddingBottom)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(PatrolEmerald.copy(alpha = 0.35f), PatrolEmerald.copy(alpha = 0.02f))
                    )
                )

                drawPath(
                    path = linePath,
                    color = PatrolEmerald,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Nodes
            points.forEachIndexed { i, pt ->
                val isSelected = selectedIndex == i

                if (isSelected) {
                    drawCircle(color = PatrolGold.copy(alpha = 0.3f), radius = 12.dp.toPx(), center = pt)
                    drawCircle(color = PatrolGold, radius = 8.dp.toPx(), center = pt, style = Stroke(2.dp.toPx()))
                }

                drawCircle(
                    color = if (isSelected) PatrolGold else PatrolEmerald,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                    center = pt
                )
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pt)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X Axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val dateFormat = SimpleDateFormat("MM/dd", Locale.US)
            validLogs.forEachIndexed { idx, log ->
                val isSelected = selectedIndex == idx
                Text(
                    text = dateFormat.format(Date(log.fillDate)),
                    fontSize = 10.sp,
                    color = if (isSelected) PatrolGold else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onPointSelected(idx) }
                )
            }
        }
    }
}

@Composable
fun FuelCostBarChart(
    logs: List<FuelLog>,
    selectedIndex: Int?,
    onBarSelected: (Int) -> Unit
) {
    val maxCost = (logs.maxOfOrNull { it.totalCostSar } ?: 250.0).coerceAtLeast(100.0)

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(logs) {
                    detectTapGestures { offset ->
                        val sectionWidth = size.width / logs.size
                        val idx = (offset.x / sectionWidth).toInt().coerceIn(logs.indices)
                        onBarSelected(idx)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val count = logs.size
            val sectionWidth = width / count
            val barWidth = sectionWidth * 0.45f

            // Grid lines
            for (i in 0..3) {
                val y = height * (i.toFloat() / 3)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw bars
            logs.forEachIndexed { i, log ->
                val isSelected = selectedIndex == i
                val costFraction = (log.totalCostSar / maxCost).toFloat().coerceIn(0.05f, 1f)
                val barHeight = (height - 24.dp.toPx()) * costFraction
                val x = i * sectionWidth + (sectionWidth - barWidth) / 2
                val yTop = height - barHeight

                val brush = Brush.verticalGradient(
                    colors = if (isSelected) listOf(PatrolGold, PatrolAmber)
                    else listOf(PatrolGold.copy(alpha = 0.8f), PatrolGold.copy(alpha = 0.4f))
                )

                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(x, yTop),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                if (isSelected) {
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(x - 2, yTop - 2),
                        size = Size(barWidth + 4, barHeight + 4),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        style = Stroke(2.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val dateFormat = SimpleDateFormat("MM/dd", Locale.US)
            logs.forEachIndexed { idx, log ->
                val isSelected = selectedIndex == idx
                Text(
                    text = dateFormat.format(Date(log.fillDate)),
                    fontSize = 10.sp,
                    color = if (isSelected) PatrolGold else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onBarSelected(idx) }
                )
            }
        }
    }
}

@Composable
fun FuelLogCard(
    log: FuelLog,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - h:mm a", Locale("ar"))
    val dateStr = dateFormat.format(Date(log.fillDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fuel_log_item_${log.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Date & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PatrolGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = "Fuel",
                            tint = PatrolGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "عداد السيارة: ${log.odometerKm} كم",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_fuel_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Info Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("الوقود المعبأ", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${log.fuelLiters} لتر (${log.fuelType})",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Column {
                    Text("المبلغ الإجمالي", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${log.totalCostSar} ريال",
                        fontWeight = FontWeight.Bold,
                        color = PatrolGold,
                        fontSize = 13.sp
                    )
                }

                Column {
                    Text("المسافة المقطوعة", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = if (log.distanceDrivenKm > 0) "${log.distanceDrivenKm} كم" else "--",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            // Consumption Badge if available
            if (log.litersPer100Km > 0) {
                Spacer(modifier = Modifier.height(10.dp))

                val (badgeColor, ratingText) = when {
                    log.litersPer100Km < 14.5 -> PatrolEmerald to "ممتاز"
                    log.litersPer100Km <= 17.5 -> PatrolAmber to "طبيعي"
                    else -> PatrolCrimson to "مرتفع"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(Locale.US, "معدل الاستهلاك: %.1f لتر / 100 كم  (%.2f كم/لتر)", log.litersPer100Km, log.kmPerLiter),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )

                    Text(
                        text = ratingText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            if (log.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظات: ${log.notes}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
