package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.PartSchedule
import com.example.ui.theme.PatrolGold

@Composable
fun EditPartDialog(
    currentKm: Int,
    onDismiss: () -> Unit,
    onConfirm: (PartSchedule) -> Unit
) {
    var partName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("صيانة مخصصة") }
    var intervalKm by remember { mutableStateOf("10000") }
    var intervalMonths by remember { mutableStateOf("6") }
    var lastServiceKm by remember { mutableStateOf(currentKm.toString()) }
    var specification by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة قطعة / صيانة جديدة للجدول") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = partName,
                    onValueChange = { partName = it },
                    label = { Text("اسم القطعة (مثال: سير المكينة والخراطيم)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_part_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("التصنيف (مثال: محرك, فرامل, كهرباء)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = intervalKm,
                    onValueChange = { intervalKm = it.filter { c -> c.isDigit() } },
                    label = { Text("دورة التغيير بالكيلومترات (مثال: 10000 كم)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = intervalMonths,
                    onValueChange = { intervalMonths = it.filter { c -> c.isDigit() } },
                    label = { Text("دورة التغيير بالشهور (مثال: 6 أشهر)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastServiceKm,
                    onValueChange = { lastServiceKm = it.filter { c -> c.isDigit() } },
                    label = { Text("آخر قراءة عداد تم التغيير عندها") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specification,
                    onValueChange = { specification = it },
                    label = { Text("المواصفة / الرقم المرجعي للقطعة") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يرجى كتابة اسم القطعة وقيم الكيلومترات بشكل صحيح",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ikm = intervalKm.toIntOrNull()
                    val imonths = intervalMonths.toIntOrNull() ?: 6
                    val lkm = lastServiceKm.toIntOrNull() ?: currentKm

                    if (partName.isNotBlank() && ikm != null && ikm > 0) {
                        val newPart = PartSchedule(
                            vehicleId = 1L,
                            partNameAr = partName,
                            category = category,
                            intervalKm = ikm,
                            intervalMonths = imonths,
                            lastServiceKm = lkm,
                            lastServiceDate = System.currentTimeMillis(),
                            specification = specification,
                            isCustomPart = true
                        )
                        onConfirm(newPart)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("save_custom_part_button")
            ) {
                Text("إضافة للجدول")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
