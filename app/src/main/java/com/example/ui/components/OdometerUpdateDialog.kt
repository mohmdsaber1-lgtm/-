package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.ui.theme.PatrolGold

@Composable
fun OdometerUpdateDialog(
    currentKm: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var kmInput by remember { mutableStateOf(currentKm.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث عداد الكيلومترات (العداد الحالي)") },
        text = {
            Column {
                Text(
                    text = "أدخل القراءة الحالية لعداد المسافات بـ الكيلومتر لنيسان باترول 2015:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = kmInput,
                    onValueChange = {
                        kmInput = it.filter { char -> char.isDigit() }
                        isError = false
                    },
                    label = { Text("الكيلومترات (كم)") },
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("odometer_input_field")
                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يرجى إدخال رقم صحيح للكيلومترات",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = kmInput.toIntOrNull()
                    if (km != null && km >= 0) {
                        onConfirm(km)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("save_odometer_button")
            ) {
                Text("حفظ القراءة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
