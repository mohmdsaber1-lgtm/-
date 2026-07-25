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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.ServiceLog
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MonthlyData(
    val monthLabel: String,
    val count: Int,
    val totalCost: Double
)

data class CategorySlice(
    val category: String,
    val cost: Double,
    val percentage: Float,
    val color: Color
)

data class TrendPoint(
    val label: String,
    val cost: Double,
    val serviceCount: Int,
    val topItems: List<String>
)

enum class TrendGroupingMode {
    MILEAGE_INTERVALS,
    TIME_MONTHLY
}

enum class ChartStyle {
    LINE_CURVE,
    BAR_COLUMNS
}

@Composable
fun AnalyticsScreen(
    serviceLogs: List<ServiceLog>,
    totalCost: Double
) {
    val formattedTotalCost = NumberFormat.getNumberInstance(Locale.US).format(totalCost)

    // Category Color Palette
    val categoryColors = listOf(
        PatrolGold,
        PatrolEmerald,
        PatrolAmber,
        Color(0xFF3B82F6), // Blue
        PatrolCrimson,
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFF14B8A6)  // Teal
    )

    // Calculate Monthly Maintenance Data (Last 6 Months)
    val monthlyDataList = remember(serviceLogs) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale("ar"))
        val result = mutableListOf<MonthlyData>()

        for (i in 5 downTo 0) {
            val c = calendar.clone() as Calendar
            c.add(Calendar.MONTH, -i)
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)

            val monthName = monthFormat.format(c.time)

            val logsInMonth = serviceLogs.filter { log ->
                val logCal = Calendar.getInstance().apply { timeInMillis = log.serviceDate }
                logCal.get(Calendar.YEAR) == year && logCal.get(Calendar.MONTH) == month
            }

            val count = logsInMonth.size
            val costSum = logsInMonth.sumOf { it.costSar }

            result.add(MonthlyData(monthLabel = monthName, count = count, totalCost = costSum))
        }
        result
    }

    // Mileage Interval Trend Points
    val mileageTrendPoints = remember(serviceLogs) {
        val ranges = listOf(
            0 to 50000 to "0-50 ألف كم",
            50001 to 100000 to "50-100 ألف كم",
            100001 to 150000 to "100-150 ألف كم",
            150001 to 200000 to "150-200 ألف كم",
            200001 to 999999 to "+200 ألف كم"
        )

        ranges.map { (rangePair, label) ->
            val minKm = rangePair.first
            val maxKm = rangePair.second
            val logsInRange = serviceLogs.filter { it.odometerKm in minKm..maxKm }
            val sumCost = logsInRange.sumOf { it.costSar }
            val topParts = logsInRange.map { it.partName }.filter { it.isNotBlank() }.distinct().take(3)

            TrendPoint(
                label = label,
                cost = sumCost,
                serviceCount = logsInRange.size,
                topItems = topParts
            )
        }
    }

    // Monthly Time Trend Points (Chronological)
    val timeTrendPoints = remember(serviceLogs) {
        monthlyDataList.map { m ->
            val logsInMonth = serviceLogs.filter { log ->
                val cal = Calendar.getInstance().apply { timeInMillis = log.serviceDate }
                val monthFormat = SimpleDateFormat("MMM", Locale("ar"))
                monthFormat.format(cal.time) == m.monthLabel
            }
            val topParts = logsInMonth.map { it.partName }.filter { it.isNotBlank() }.distinct().take(3)

            TrendPoint(
                label = m.monthLabel,
                cost = m.totalCost,
                serviceCount = m.count,
                topItems = topParts
            )
        }
    }

    // Calculate Category Distribution Data
    val categorySlices = remember(serviceLogs, totalCost) {
        val grouped = serviceLogs.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.costSar } }
            .entries.sortedByDescending { it.value }

        grouped.mapIndexed { index, entry ->
            val pct = if (totalCost > 0) (entry.value / totalCost).toFloat() else 0f
            CategorySlice(
                category = entry.key,
                cost = entry.value,
                percentage = pct,
                color = categoryColors[index % categoryColors.size]
            )
        }
    }

    var selectedMonthIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = "إحصائيات وتكاليف صيانة الباترول",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "تحليل وتيرة الصيانة والتوجهات المالية وتوزيع المصاريف",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Total Expenses KPI Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_total_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(PatrolGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Cost",
                            tint = PatrolGold,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "إجمالي التكاليف المسجلة",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "$formattedTotalCost ريال سعودي",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PatrolEmerald
                        )
                        Text(
                            text = "إجمالي عدد عمليات الإصلاح والصيانة: ${serviceLogs.size} عملية",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // 1. Maintenance Cost Trends Chart (Line / Mileage / Time Chart)
        item {
            CostTrendChartCard(
                mileagePoints = mileageTrendPoints,
                timePoints = timeTrendPoints
            )
        }

        // 2. Monthly Maintenance Frequency Chart Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_chart_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Bar Chart",
                                tint = PatrolGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "وتيرة الصيانة الشهرية (آخر 6 أشهر)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "اضغط للتفاصيل",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Compose Canvas Bar Chart
                    MonthlyBarChart(
                        monthlyDataList = monthlyDataList,
                        selectedIndex = selectedMonthIndex,
                        onBarSelected = { idx -> selectedMonthIndex = idx }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Month Selection Tooltip
                    selectedMonthIndex?.let { index ->
                        val data = monthlyDataList[index]
                        val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(data.totalCost)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PatrolGold.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Month",
                                        tint = PatrolGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "شهر ${data.monthLabel}:",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${data.count} عمليات صيانة | $formattedCost ريال",
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Repair Cost Distribution Donut Chart Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cost_distribution_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Pie Chart",
                            tint = PatrolGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توزيع التكاليف حسب نوع القطعة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (categorySlices.isEmpty()) {
                        Text(
                            text = "لا توجد مصاريف مسجلة حتى الآن لحساب الرسم البياني.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        // Donut Visualizer
                        DonutChart(
                            slices = categorySlices,
                            totalCost = totalCost
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Breakdown Legends List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            categorySlices.forEach { slice ->
                                val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(slice.cost)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(slice.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = slice.category,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                    }

                                    Text(
                                        text = "$formattedCost ريال (${(slice.percentage * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = slice.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Agency Tip Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = PatrolAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "توصية نيسان لباترول 2015 V8 VK56",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "المحافظة على تغيير زيت القير الدورية (كل 40,000 كم) وبواجي الليزر (كل 80,000 كم) واستخدام ماء الرديتر نيسان الأزرق الأصلي يحافظ على عمر المحرك الناعم ويمنع الحرارة تماماً.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CostTrendChartCard(
    mileagePoints: List<TrendPoint>,
    timePoints: List<TrendPoint>
) {
    var groupingMode by remember { mutableStateOf(TrendGroupingMode.MILEAGE_INTERVALS) }
    var chartStyle by remember { mutableStateOf(ChartStyle.LINE_CURVE) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val points = if (groupingMode == TrendGroupingMode.MILEAGE_INTERVALS) mileagePoints else timePoints
    val maxCost = (points.maxOfOrNull { it.cost } ?: 1.0).coerceAtLeast(100.0)
    val avgCost = if (points.isNotEmpty()) points.sumOf { it.cost } / points.size else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cost_trend_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Cost Trends",
                        tint = PatrolEmerald
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "منحنى توجهات مصاريف الصيانة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = if (groupingMode == TrendGroupingMode.MILEAGE_INTERVALS) "مسافة العداد" else "الجدول الزمني",
                    fontSize = 11.sp,
                    color = PatrolGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grouping and Chart Style Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Grouping Selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = groupingMode == TrendGroupingMode.MILEAGE_INTERVALS,
                        onClick = {
                            groupingMode = TrendGroupingMode.MILEAGE_INTERVALS
                            selectedIndex = null
                        },
                        label = { Text("🛣️ الكيلومترات", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PatrolGold,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.Black.copy(alpha = 0.3f),
                            labelColor = Color.LightGray
                        )
                    )

                    FilterChip(
                        selected = groupingMode == TrendGroupingMode.TIME_MONTHLY,
                        onClick = {
                            groupingMode = TrendGroupingMode.TIME_MONTHLY
                            selectedIndex = null
                        },
                        label = { Text("📅 الأشهر", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PatrolGold,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.Black.copy(alpha = 0.3f),
                            labelColor = Color.LightGray
                        )
                    )
                }

                // Style Switcher (Line vs Bar)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (chartStyle == ChartStyle.LINE_CURVE) PatrolEmerald else Color.Transparent)
                            .clickable { chartStyle = ChartStyle.LINE_CURVE }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Line Curve",
                            tint = if (chartStyle == ChartStyle.LINE_CURVE) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (chartStyle == ChartStyle.BAR_COLUMNS) PatrolEmerald else Color.Transparent)
                            .clickable { chartStyle = ChartStyle.BAR_COLUMNS }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Bar Columns",
                            tint = if (chartStyle == ChartStyle.BAR_COLUMNS) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Interactive Canvas Chart
            if (chartStyle == ChartStyle.LINE_CURVE) {
                TrendLineCanvas(
                    points = points,
                    maxCost = maxCost,
                    avgCost = avgCost,
                    selectedIndex = selectedIndex,
                    onPointSelected = { idx -> selectedIndex = idx }
                )
            } else {
                MonthlyBarChart(
                    monthlyDataList = points.map { MonthlyData(it.label, it.serviceCount, it.cost) },
                    selectedIndex = selectedIndex,
                    onBarSelected = { idx -> selectedIndex = idx }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Point Detail Inspection Card
            selectedIndex?.let { idx ->
                val p = points.getOrNull(idx)
                if (p != null) {
                    val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(p.cost)
                    val formattedAvg = NumberFormat.getNumberInstance(Locale.US).format(avgCost)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PatrolEmerald.copy(alpha = 0.15f))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (groupingMode == TrendGroupingMode.MILEAGE_INTERVALS) Icons.Default.Speed else Icons.Default.Timeline,
                                        contentDescription = "Detail",
                                        tint = PatrolEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "الفترة: ${p.label}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = "$formattedCost ريال",
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolEmerald,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "عدد الصيانات: ${p.serviceCount} عمليات",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "المتوسط العام: $formattedAvg ريال",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            if (p.topItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "أبرز القطع المصلحة: ${p.topItems.joinToString(" • ")}",
                                    fontSize = 11.sp,
                                    color = PatrolGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendLineCanvas(
    points: List<TrendPoint>,
    maxCost: Double,
    avgCost: Double,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit
) {
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        if (points.isEmpty()) return@detectTapGestures
                        val sectionWidth = size.width / points.size
                        val index = (offset.x / sectionWidth).toInt().coerceIn(points.indices)
                        onPointSelected(index)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val count = points.size
            if (count == 0) return@Canvas

            val sectionWidth = width / count
            val paddingBottom = 24.dp.toPx()
            val paddingTop = 20.dp.toPx()
            val usableHeight = height - paddingTop - paddingBottom

            // 1. Draw Grid Lines & Max Value Markers
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

            // 2. Draw Average Cost Dashed Line
            if (avgCost > 0 && maxCost > 0) {
                val avgY = paddingTop + usableHeight * (1f - (avgCost / maxCost).toFloat().coerceIn(0f, 1f))
                drawLine(
                    color = PatrolGold.copy(alpha = 0.6f),
                    start = Offset(0f, avgY),
                    end = Offset(width, avgY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            }

            // Calculate coordinates for each point
            val offsets = points.mapIndexed { index, p ->
                val x = index * sectionWidth + sectionWidth / 2
                val fraction = if (maxCost > 0) (p.cost / maxCost).toFloat().coerceIn(0f, 1f) else 0f
                val y = paddingTop + usableHeight * (1f - fraction)
                Offset(x, y)
            }

            // 3. Draw Bezier Curve & Gradient Area Fill
            val linePath = Path()
            val fillPath = Path()

            if (offsets.isNotEmpty()) {
                linePath.moveTo(offsets[0].x, offsets[0].y)
                fillPath.moveTo(offsets[0].x, height - paddingBottom)
                fillPath.lineTo(offsets[0].x, offsets[0].y)

                for (i in 0 until offsets.size - 1) {
                    val p1 = offsets[i]
                    val p2 = offsets[i + 1]
                    val controlX1 = p1.x + (p2.x - p1.x) / 2
                    val controlY1 = p1.y
                    val controlX2 = p1.x + (p2.x - p1.x) / 2
                    val controlY2 = p2.y

                    linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                }

                fillPath.lineTo(offsets.last().x, height - paddingBottom)
                fillPath.close()

                // Draw Area Fill Under Line
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PatrolEmerald.copy(alpha = 0.35f),
                            PatrolEmerald.copy(alpha = 0.02f)
                        )
                    )
                )

                // Draw Smooth Line Path
                drawPath(
                    path = linePath,
                    color = PatrolEmerald,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 4. Draw Data Node Points
            offsets.forEachIndexed { index, pointOffset ->
                val isSelected = selectedIndex == index
                val pointCost = points[index].cost

                // Outer Ring / Glow
                if (isSelected) {
                    drawCircle(
                        color = PatrolGold.copy(alpha = 0.3f),
                        radius = 12.dp.toPx(),
                        center = pointOffset
                    )
                    drawCircle(
                        color = PatrolGold,
                        radius = 8.dp.toPx(),
                        center = pointOffset,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Node Center
                drawCircle(
                    color = if (isSelected) PatrolGold else if (pointCost > 0) PatrolEmerald else Color.Gray,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                    center = pointOffset
                )

                // White center dot
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = pointOffset
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // X-Axis Labels Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            points.forEachIndexed { index, p ->
                val isSelected = selectedIndex == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onPointSelected(index) }
                ) {
                    Text(
                        text = p.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PatrolGold else Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyBarChart(
    monthlyDataList: List<MonthlyData>,
    selectedIndex: Int?,
    onBarSelected: (Int) -> Unit
) {
    val maxCost = (monthlyDataList.maxOfOrNull { it.totalCost } ?: 1.0).coerceAtLeast(100.0)

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(monthlyDataList) {
                    detectTapGestures { offset ->
                        val barWidthWidthSpace = size.width / monthlyDataList.size
                        val index = (offset.x / barWidthWidthSpace).toInt()
                        if (index in monthlyDataList.indices) {
                            onBarSelected(index)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val itemCount = monthlyDataList.size
            val sectionWidth = canvasWidth / itemCount
            val barWidth = sectionWidth * 0.45f

            // Draw background horizontal grid lines
            val gridLineCount = 3
            for (i in 0..gridLineCount) {
                val y = canvasHeight * (i.toFloat() / gridLineCount)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw bars
            monthlyDataList.forEachIndexed { index, data ->
                val isSelected = selectedIndex == index
                val costFraction = (data.totalCost / maxCost).toFloat().coerceIn(0.05f, 1f)
                val barHeight = (canvasHeight - 30.dp.toPx()) * costFraction

                val xOffset = index * sectionWidth + (sectionWidth - barWidth) / 2
                val yTop = canvasHeight - barHeight

                val barBrush = Brush.verticalGradient(
                    colors = if (isSelected) {
                        listOf(PatrolGold, PatrolAmber)
                    } else if (data.totalCost > 0) {
                        listOf(PatrolGold.copy(alpha = 0.8f), PatrolGold.copy(alpha = 0.4f))
                    } else {
                        listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                    }
                )

                // Draw Bar
                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(xOffset, yTop),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Selection Border
                if (isSelected) {
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(xOffset - 2, yTop - 2),
                        size = Size(barWidth + 4, barHeight + 4),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month Labels & Count Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            monthlyDataList.forEachIndexed { index, data ->
                val isSelected = selectedIndex == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onBarSelected(index) }
                ) {
                    Text(
                        text = data.monthLabel,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PatrolGold else Color.LightGray
                    )
                    Text(
                        text = if (data.count > 0) "${data.count}ص" else "-",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    slices: List<CategorySlice>,
    totalCost: Double
) {
    val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalCost)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 26.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            var startAngle = -90f

            slices.forEach { slice ->
                val sweepAngle = slice.percentage * 360f
                if (sweepAngle > 0f) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f, // 2deg gap
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "الإجمالي",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = "$formattedTotal ر.س",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PatrolGold
            )
        }
    }
}
