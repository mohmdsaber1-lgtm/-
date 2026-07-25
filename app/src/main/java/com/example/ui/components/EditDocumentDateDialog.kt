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
fun EditDocumentDateDialog(
    documentName: String,
    currentDaysLeft: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var daysInput by remember { mutableStateOf(currentDaysLeft.coerceAtLeast(0).toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث موعد انتهاء: $documentName") },
        text = {
            Column {
                Text(
                    text = "أدخل عدد الأيام المتبقية حتى تاريخ انتهاء $documentName:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = daysInput,
                    onValueChange = {
                        daysInput = it.filter { char -> char.isDigit() }
                        isError = false
                    },
                    label = { Text("الأيام المتبقية (يوم)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("days_remaining_input")
                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يرجى أدخل رقم صحيح للأيام",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = daysInput.toLongOrNull()
                    if (days != null) {
                        val futureTime = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)
                        onConfirm(futureTime)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PatrolGold),
                modifier = Modifier.testTag("save_document_days_button")
            ) {
                Text("حفظ الموعد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
