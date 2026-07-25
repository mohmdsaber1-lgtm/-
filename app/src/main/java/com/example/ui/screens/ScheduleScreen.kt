package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceStatus
import com.example.data.model.PartSchedule
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ScheduleScreen(
    partSchedules: List<PartSchedule>,
    currentOdometer: Int,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onLogService: (PartSchedule) -> Unit,
    onAddCustomPart: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "الكل",
        "زيوت وسوائل",
        "فلاتر",
        "فرامل",
        "محرك وشمعات",
        "قير ودفرنش",
        "كهرباء وبطارية",
        "إطارات وهيكل"
    )

    val filteredList = partSchedules.filter { part ->
        val matchesCategory = (selectedCategory == "الكل" || part.category == selectedCategory)
        val matchesSearch = part.partNameAr.contains(searchQuery, ignoreCase = true) ||
                part.specification.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomPart,
                containerColor = PatrolGold,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_custom_part_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة قطعة صيانة")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "جدول صيانة وكالة نيسان باترول 2015",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "مواعيد الصيانة الدورية الموصى بها لقطع وسوائل السيارة",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث عن قطعة أو زيت...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = (selectedCategory == category),
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PatrolGold,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد نتائج مطابقة للبحث أو التصنيف", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList) { part ->
                        PartScheduleCard(
                            part = part,
                            currentOdometer = currentOdometer,
                            onLogService = { onLogService(part) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PartScheduleCard(
    part: PartSchedule,
    currentOdometer: Int,
    onLogService: () -> Unit
) {
    val status = part.getStatus(currentOdometer)
    val remainingKm = part.getRemainingKm(currentOdometer)
    val nextDueKm = part.getNextDueKm()

    val (statusLabel, badgeColor) = when (status) {
        MaintenanceStatus.DUE_NOW -> "مستحق الآن!" to PatrolCrimson
        MaintenanceStatus.DUE_SOON -> "قريباً" to PatrolAmber
        MaintenanceStatus.GOOD -> "ساري - حالة جيدة" to PatrolEmerald
    }

    val formattedNextDue = NumberFormat.getNumberInstance(Locale.US).format(nextDueKm)
    val formattedLast = NumberFormat.getNumberInstance(Locale.US).format(part.lastServiceKm)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("part_schedule_item_${part.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Part",
                        tint = PatrolGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = part.partNameAr,
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

            Spacer(modifier = Modifier.height(8.dp))

            if (part.specification.isNotBlank()) {
                Text(
                    text = "المواصفة: ${part.specification}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الجدول الدوري: كل ${part.intervalKm} كم (${part.intervalMonths} شهر)",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "آخر صيانة: $formattedLast كم",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = "الصيانة القادمة: $formattedNextDue كم",
                    style = MaterialTheme.typography.labelMedium,
                    color = PatrolGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLogService,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_service_part_btn_${part.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تسجيل تغيير / صيانة القطعة الآن")
            }
        }
    }
}
