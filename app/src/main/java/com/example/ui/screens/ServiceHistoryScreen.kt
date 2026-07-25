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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ServiceLog
import com.example.data.model.Vehicle
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolCrimson
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import com.example.utils.ExportUtils
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServiceHistoryScreen(
    serviceLogs: List<ServiceLog>,
    totalCost: Double,
    vehicle: Vehicle? = null,
    onAddNewLog: () -> Unit,
    onDeleteLog: (ServiceLog) -> Unit
) {
    val context = LocalContext.current
    val formattedTotalCost = NumberFormat.getNumberInstance(Locale.US).format(totalCost)
    var selectedPhotoPathForViewer by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(serviceLogs, searchQuery) {
        if (searchQuery.isBlank()) {
            serviceLogs
        } else {
            val query = searchQuery.trim()
            serviceLogs.filter { log ->
                log.partName.contains(query, ignoreCase = true) ||
                log.category.contains(query, ignoreCase = true) ||
                log.serviceType.contains(query, ignoreCase = true) ||
                log.notes.contains(query, ignoreCase = true) ||
                log.workshopName.contains(query, ignoreCase = true) ||
                log.invoiceNumber.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Total Expense Card Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PatrolCardDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إجمالي مصروفات صيانة الباترول",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$formattedTotalCost ريال سعودي",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PatrolGold
                        )
                    }

                    Button(
                        onClick = onAddNewLog,
                        colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_history_log_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تسجيل صيانة")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export Options Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { ExportUtils.exportToPdf(context, vehicle, serviceLogs) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_pdf_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PatrolGold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصدير PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { ExportUtils.exportToCsv(context, vehicle, serviceLogs) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_csv_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PatrolEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "CSV",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصدير CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث باسم القطعة، التصنيف، الملاحظات، أو الورشة...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PatrolGold
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = Color.Gray
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PatrolGold,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedContainerColor = PatrolCardDark,
                unfocusedContainerColor = PatrolCardDark,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = PatrolGold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (searchQuery.isBlank()) {
                    "سجل العمليات والإصلاحات المسجلة (${serviceLogs.size})"
                } else {
                    "نتائج البحث (${filteredLogs.size} من أصل ${serviceLogs.size})"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (serviceLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد أي سجلات إصلاح سابقة محفوظة بعد.",
                    color = Color.Gray
                )
            }
        } else if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد نتائج تطابق البحث \"$searchQuery\"",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredLogs) { log ->
                    ServiceLogCard(
                        log = log,
                        onDelete = { onDeleteLog(log) },
                        onPhotoClick = { path -> selectedPhotoPathForViewer = path }
                    )
                }
            }
        }
    }

    // Full Screen Photo Viewer Modal
    selectedPhotoPathForViewer?.let { photoPath ->
        Dialog(onDismissRequest = { selectedPhotoPathForViewer = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PatrolCardDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صورة الفاتورة / القطعة",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = { selectedPhotoPathForViewer = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = "Full Invoice Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceLogCard(
    log: ServiceLog,
    onDelete: () -> Unit,
    onPhotoClick: (String) -> Unit = {}
) {
    val dateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("ar")).format(Date(log.serviceDate))
    val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(log.costSar)
    val formattedKm = NumberFormat.getNumberInstance(Locale.US).format(log.odometerKm)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_log_item_${log.id}"),
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
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Invoice",
                        tint = PatrolEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.partName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = PatrolCrimson
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "العداد: $formattedKm كم",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PatrolGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "التكلفة: $formattedCost ريال",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PatrolEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "المركز: ${log.workshopName.ifBlank { "غير محدد" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظات: ${log.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }

            // Display Attached Image Preview if available
            if (!log.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PatrolGold.copy(alpha = 0.12f))
                        .border(1.dp, PatrolGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { onPhotoClick(log.imageUri) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = File(log.imageUri),
                        contentDescription = "Invoice Photo Thumbnail",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Photo attached",
                                tint = PatrolGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مرفق صورة الفاتورة / القطعة",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "اضغط لعرض الصورة بحجم كامل",
                            style = MaterialTheme.typography.labelSmall,
                            color = PatrolGold
                        )
                    }
                }
            }
        }
    }
}
