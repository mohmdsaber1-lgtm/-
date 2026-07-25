package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.PartSchedule
import com.example.data.model.Vehicle

object NotificationHelper {

    const val CHANNEL_ID = "patrol_maintenance_channel"
    private const val CHANNEL_NAME = "تنبيهات صيانة الباترول والاستمارة"
    private const val CHANNEL_DESC = "إشعارات تذكير بمواعيد صيانة قطع السيارة وانتهاء الاستمارة والفحص الدوري"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(id, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun sendTestNotification(context: Context) {
        sendNotification(
            context,
            1001,
            "🚗 تنبيه صيانة نيسان باترول",
            "تم تفعيل التنبيهات بنجاح! سيتم تذكيرك التلقائي قبل حلول موعد صيانة القطع، وانتهاء الاستمارة والفحص الدوري."
        )
    }

    fun sendFahsAlertNotification(context: Context, daysLeft: Long) {
        val title = "⚠️ اقتراب انتهاء الفحص الدوري لباترول 2015"
        val msg = if (daysLeft <= 0) {
            "عفواً، الفحص الدوري لمركبتك منتهي حالياً! يرجى تجديد الفحص."
        } else {
            "متبقي $daysLeft يوم على انتهاء الفحص الدوري. يرجى حجز موعد محطة الفحص."
        }
        sendNotification(context, 2001, title, msg)
    }

    fun sendIstimaraAlertNotification(context: Context, daysLeft: Long) {
        val title = "📑 اقتراب انتهاء استمارة السيارة"
        val msg = if (daysLeft <= 0) {
            "استمارة السيارة منتهية! يرجى تجديد رخصة السير عبر ابشر."
        } else {
            "متبقي $daysLeft يوم على انتهاء استمارة المركبة."
        }
        sendNotification(context, 2002, title, msg)
    }

    fun sendPartMaintenanceNotification(context: Context, partName: String, remainingKm: Int) {
        val title = "🔧 تنبيه صيانة مستحقة: $partName"
        val msg = if (remainingKm <= 0) {
            "تنبيه: حان موعد استبدال/صيانة $partName لنيسان باترول!"
        } else {
            "متبقي $remainingKm كم فقط على موعد صيانة $partName."
        }
        sendNotification(context, (3000 + (partName.hashCode() and 0x7FFFFFFF) % 1000), title, msg)
    }

    /**
     * Checks all maintenance items for Nissan Patrol 2015 against remaining Km or remaining Days.
     * Triggers real Android system notifications for any due/due soon parts or expiring documents.
     */
    fun checkAndSendAllDueNotifications(
        context: Context,
        vehicle: Vehicle?,
        partSchedules: List<PartSchedule>,
        kmThreshold: Int = 1000,
        daysThreshold: Int = 15
    ): Int {
        var alertCount = 0
        val odo = vehicle?.currentOdometer ?: 185000

        partSchedules.forEach { part ->
            val remKm = part.getRemainingKm(odo)
            val remDays = part.getRemainingDays()

            if (remKm <= kmThreshold || remDays <= daysThreshold) {
                alertCount++
                val notifId = 30000 + (part.id.toInt() and 0x7FFF)
                val title = if (remKm <= 0 || remDays <= 0) {
                    "🔴 صيانة مستحقة الآن: ${part.partNameAr}"
                } else {
                    "⚠️ اقتراب موعد صيانة: ${part.partNameAr}"
                }

                val msg = StringBuilder().apply {
                    if (remKm <= 0) {
                        append("تجاوزت مسافة الصيانة بـ ${-remKm} كم! ")
                    } else if (remKm <= kmThreshold) {
                        append("متبقي $remKm كم فقط. ")
                    }

                    if (remDays <= 0) {
                        append("تجاوزت التاريخ بـ ${-remDays} يوم. ")
                    } else if (remDays <= daysThreshold) {
                        append("متبقي $remDays يوم. ")
                    }

                    if (part.specification.isNotEmpty()) {
                        append("\nالمواصفة: ${part.specification}")
                    }
                }.toString()

                sendNotification(context, notifId, title, msg)
            }
        }

        // Vehicle Document check
        vehicle?.let { v ->
            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            val fahsDaysLeft = (v.fahsExpiryDate - now) / dayMs
            if (fahsDaysLeft <= daysThreshold) {
                alertCount++
                sendFahsAlertNotification(context, fahsDaysLeft)
            }

            val istimaraDaysLeft = (v.istimaraExpiryDate - now) / dayMs
            if (istimaraDaysLeft <= daysThreshold) {
                alertCount++
                sendIstimaraAlertNotification(context, istimaraDaysLeft)
            }
        }

        return alertCount
    }

    fun scheduleDailyAlarm(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                action = "CHECK_PATROL_MAINTENANCE"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                8888,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAtMs = System.currentTimeMillis() + (12 * 60 * 60 * 1000L) // check every 12 hours
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMs,
                AlarmManager.INTERVAL_HALF_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

