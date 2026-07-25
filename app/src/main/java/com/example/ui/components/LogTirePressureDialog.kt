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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.TirePressureLog
import com.example.ui.theme.PatrolAmber
import com.example.ui.theme.PatrolCardDark
import com.example.ui.theme.PatrolDarkCharcoal
import com.example.ui.theme.PatrolGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogTirePressureDialog(
    currentVehicleKm: Int,
    onDismiss: () -> Unit,
    onConfirm: (TirePressureLog) -> Unit
) {
    var odometerInput by remember { mutableStateOf(currentVehicleKm.toString()) }
    var drivingContext by remember { mutableStateOf("مدينة") }
    var notesInput by remember { mutableStateOf("") }

    var flPsi by remember { mutableDoubleStateOf(35.0) }
    var frPsi by remember { mutableDoubleStateOf(35.0) }
    var rlPsi by remember { mutableDoubleStateOf(35.0) }
    var rrPsi by remember { mutableDoubleStateOf(35.0) }
    var sparePsi by remember { mutableDoubleStateOf(35.0) }

    val formattedDate = remember {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("ar")).format(Date())
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
                        imageVector = Icons.Default.TireRepair,
                        contentDescription = "Tire Pressure",
                        tint = PatrolGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "تسجيل قياس ضغط الإطارات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "تأكد من سلامة إطارات نيسان باترول",
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
                // Date & Mileage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = odometerInput,
                        onValueChange = { odometerInput = it.filter { char -> char.isDigit() } },
                        label = { Text("قراءة العداد (كم)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tire_odometer_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PatrolGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = PatrolGold,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = CardDefaults.cardColors(containerColor = PatrolDarkCharcoal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("التاريخ والوقت:", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                formattedDate,
                                fontSize = 11.sp,
                                color = PatrolGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Preset Quick Action Buttons
                Text(
                    text = "ضبط سريع للضغط بحسب ظروف القيادة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "مدينة (35 PSI)" to 35.0,
                        "سفر (36 PSI)" to 36.0,
                        "رمل (18 PSI)" to 18.0
                    ).forEach { (label, presetVal) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (drivingContext.contains(label.take(3))) PatrolGold else Color.Gray.copy(alpha = 0.2f))
                                .clickable {
                                    flPsi = presetVal
                                    frPsi = presetVal
                                    rlPsi = presetVal
                                    rrPsi = presetVal
                                    if (presetVal > 25.0) sparePsi = presetVal
                                    drivingContext = when (presetVal) {
                                        18.0 -> "رحلة برية / رمل"
                                        36.0 -> "خط وسفر"
                                        else -> "مدينة"
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (drivingContext.contains(label.take(3))) Color.Black else Color.White
                            )
                        }
                    }
                }

                // Driving Context Selector
                Text(
                    text = "نوع استخدام المركبة الحالي:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("مدينة", "خط وسفر", "رحلة برية / رمل").forEach { ctx ->
                        val isSel = drivingContext == ctx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PatrolGold.copy(alpha = 0.25f) else PatrolDarkCharcoal)
                                .clickable { drivingContext = ctx }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ctx,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) PatrolGold else Color.White
                            )
                        }
                    }
                }

                // Tire Individual Sliders
                Text(
                    text = "قراءات ضغط الإطارات الأربعة والاحتياطي (PSI):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TirePsiSliderItem(
                    label = "الأمامي الأيسر (FL)",
                    value = flPsi,
                    onValueChange = { flPsi = it }
                )
                TirePsiSliderItem(
                    label = "الأمامي الأيمن (FR)",
                    value = frPsi,
                    onValueChange = { frPsi = it }
                )
                TirePsiSliderItem(
                    label = "الخلفي الأيسر (RL)",
                    value = rlPsi,
                    onValueChange = { rlPsi = it }
                )
                TirePsiSliderItem(
                    label = "الخلفي الأيمن (RR)",
                    value = rrPsi,
                    onValueChange = { rrPsi = it }
                )
                TirePsiSliderItem(
                    label = "الإطار الاحتياطي (الاستبنة)",
                    value = sparePsi,
                    onValueChange = { sparePsi = it }
                )

                // Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("ملاحظات / اسم المحطة أو الورشة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tire_notes_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PatrolGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PatrolGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = odometerInput.toIntOrNull() ?: currentVehicleKm
                    val log = TirePressureLog(
                        vehicleId = 1L,
                        recordDate = System.currentTimeMillis(),
                        odometerKm = km,
                        frontLeftPsi = ((flPsi * 10).toInt() / 10.0),
                        frontRightPsi = ((frPsi * 10).toInt() / 10.0),
                        rearLeftPsi = ((rlPsi * 10).toInt() / 10.0),
                        rearRightPsi = ((rrPsi * 10).toInt() / 10.0),
                        sparePsi = ((sparePsi * 10).toInt() / 10.0),
                        drivingContext = drivingContext,
                        notes = notesInput
                    )
                    onConfirm(log)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("save_tire_log_button")
            ) {
                Text("حفظ القراءة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun TirePsiSliderItem(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PatrolDarkCharcoal)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.sp, color = Color.White)
            val color = when {
                value < 20.0 -> PatrolAmber
                value < 31.0 -> PatrolAmber
                value > 39.0 -> PatrolAmber
                else -> PatrolGold
            }
            Text(
                text = "${(value * 10).toInt() / 10.0} PSI",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = 10f..45f,
            steps = 34,
            colors = SliderDefaults.colors(
                thumbColor = PatrolGold,
                activeTrackColor = PatrolGold,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}
