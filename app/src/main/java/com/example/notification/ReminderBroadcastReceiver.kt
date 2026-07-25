package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.PatrolDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val db = PatrolDatabase.getDatabase(context, scope)
                val vehicle = db.vehicleDao().getVehicleDirect()
                val partSchedules = db.partScheduleDao().getAllPartSchedulesDirect()

                NotificationHelper.checkAndSendAllDueNotifications(
                    context = context,
                    vehicle = vehicle,
                    partSchedules = partSchedules
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

