package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelLog
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolEmerald
import com.example.ui.theme.PatrolGold
import java.util.Locale

@Composable
fun LogFuelDialog(
    currentVehicleKm: Int,
    lastFuelOdometerKm: Int?,
    onDismiss: () -> Unit,
    onConfirm: (FuelLog) -> Unit
) {
    var odometerInput by remember { mutableStateOf(currentVehicleKm.toString()) }
    var litersInput by remember { mutableStateOf("90.0") }
    var pricePerLiterInput by remember { mutableStateOf("2.18") }
    var totalCostInput by remember { mutableStateOf("196.20") }
    var fuelType by remember { mutableStateOf("بنزين 91") }
    var isFullTank by remember { mutableStateOf(true) }
    var notesInput by remember { mutableStateOf("") }

    // Distance driven override or calculated
    val odo = odometerInput.toIntOrNull() ?: currentVehicleKm
    val lastOdo = lastFuelOdometerKm ?: currentVehicleKm
    val autoDistance = if (odo > lastOdo) odo - lastOdo else 0
    var distanceInput by remember { mutableStateOf(if (autoDistance > 0) autoDistance.toString() else "600") }

    val liters = litersInput.toDoubleOrNull() ?: 0.0
    val dist = distanceInput.toIntOrNull() ?: autoDistance
    val calculatedL100 = if (dist > 0 && liters > 0) (liters / dist) * 100 else 0.0
    val calculatedKmL = if (liters > 0 && dist > 0) dist / liters else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PatrolCardDark,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = "Refuel",
                    tint = PatrolGold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "تسجيل تعبئة وقود جديدة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Odometer field
                OutlinedTextField(
                    value = odometerInput,
                    onValueChange = { input ->
                        odometerInput = input
                        val newOdo = input.toIntOrNull()
                        if (newOdo != null && lastFuelOdometerKm != null && newOdo > lastFuelOdometerKm) {
                            distanceInput = (newOdo - lastFuelOdometerKm).toString()
                        }
                    },
                    label = { Text("عداد السيارة الحالي (كم)", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_dialog_odometer"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PatrolGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PatrolGold,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = "Odometer", tint = PatrolGold)
                    }
                )

                // Distance driven field
                OutlinedTextField(
                    value = distanceInput,
                    onValueChange = { distanceInput = it },
                    label = { Text("المسافة المقطوعة منذ التعبئة السابقة (كم)", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_dialog_distance"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PatrolGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PatrolGold,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Liters filled
                OutlinedTextField(
                    value = litersInput,
                    onValueChange = { input ->
                        litersInput = input
                        val l = input.toDoubleOrNull()
                        val p = pricePerLiterInput.toDoubleOrNull()
                        if (l != null && p != null) {
                            totalCostInput = String.format(Locale.US, "%.2f", l * p)
                        }
                    },
                    label = { Text("كمية الوقود المعبأة (لتر)", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_dialog_liters"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PatrolGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PatrolGold,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Price Per Liter and Total Cost Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = pricePerLiterInput,
                        onValueChange = { input ->
                            pricePerLiterInput = input
                            val p = input.toDoubleOrNull()
                            val l = litersInput.toDoubleOrNull()
                            if (p != null && l != null) {
                                totalCostInput = String.format(Locale.US, "%.2f", l * p)
                            }
                        },
                        label = { Text("سعر اللتر (ريال)", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fuel_dialog_price_per_liter"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PatrolGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = PatrolGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = totalCostInput,
                        onValueChange = { input ->
                            totalCostInput = input
                            val total = input.toDoubleOrNull()
                            val l = litersInput.toDoubleOrNull()
                            if (total != null && l != null && l > 0) {
                                pricePerLiterInput = String.format(Locale.US, "%.2f", total / l)
                            }
                        },
                        label = { Text("المبلغ الإجمالي (ريال)", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fuel_dialog_total_cost"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PatrolGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = PatrolGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                // Fuel Type Selection (91 / 95 / Diesel)
                Text("نوع الوقود:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("بنزين 91", "بنزين 95", "ديزل").forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (fuelType == type) PatrolGold.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    fuelType = type
                                    if (type == "بنزين 91") pricePerLiterInput = "2.18"
                                    else if (type == "بنزين 95") pricePerLiterInput = "2.33"
                                    else if (type == "ديزل") pricePerLiterInput = "1.15"

                                    val p = pricePerLiterInput.toDoubleOrNull()
                                    val l = litersInput.toDoubleOrNull()
                                    if (p != null && l != null) {
                                        totalCostInput = String.format(Locale.US, "%.2f", l * p)
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (fuelType == type),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = PatrolGold)
                            )
                            Text(text = type, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                // Full Tank Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFullTank = !isFullTank }
                ) {
                    Checkbox(
                        checked = isFullTank,
                        onCheckedChange = { isFullTank = it },
                        colors = CheckboxDefaults.colors(checkedColor = PatrolGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تعبئة خزان الوقود بالكامل (فل تانك)", color = Color.White, fontSize = 13.sp)
                }

                // Notes input
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("ملاحظات / اسم المحطة (اختياري)", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_dialog_notes"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PatrolGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PatrolGold,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Calculated Rate Live Preview
                if (calculatedL100 > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PatrolDarkCharcoal)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "معدل الاستهلاك المحسوب لهذه التعبئة:",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1f لتر / 100 كم", calculatedL100),
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolEmerald,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = String.format(Locale.US, "%.2f كم / لتر", calculatedKmL),
                                    fontWeight = FontWeight.Bold,
                                    color = PatrolGold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val odoVal = odometerInput.toIntOrNull() ?: currentVehicleKm
                    val litersVal = litersInput.toDoubleOrNull() ?: 0.0
                    val priceVal = pricePerLiterInput.toDoubleOrNull() ?: 2.18
                    val totalVal = totalCostInput.toDoubleOrNull() ?: (litersVal * priceVal)
                    val distVal = distanceInput.toIntOrNull() ?: autoDistance

                    val newFuelLog = FuelLog(
                        vehicleId = 1L,
                        fillDate = System.currentTimeMillis(),
                        odometerKm = odoVal,
                        fuelLiters = litersVal,
                        pricePerLiter = priceVal,
                        totalCostSar = totalVal,
                        distanceDrivenKm = distVal,
                        fuelType = fuelType,
                        isFullTank = isFullTank,
                        notes = notesInput.trim()
                    )

                    onConfirm(newFuelLog)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("fuel_dialog_confirm")
            ) {
                Text("حفظ التعبئة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Gray)
            }
        }
    )
}
