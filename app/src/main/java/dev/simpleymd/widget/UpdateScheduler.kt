package dev.simpleymd.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object UpdateScheduler {
    const val ACTION_MIDNIGHT = "dev.simpleymd.widget.ACTION_MIDNIGHT_UPDATE"

    fun scheduleNextMidnight(context: Context) {
        try {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, SinceWidgetProvider::class.java).setAction(ACTION_MIDNIGHT)
            val pi = PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val next = LocalDate.now().plusDays(1).atStartOfDay().plusMinutes(1)
            val millis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC, millis, pi)
            } catch (_: SecurityException) {
                am.setAndAllowWhileIdle(AlarmManager.RTC, millis, pi)
            }
        } catch (_: Exception) {
            // Widget must still render even if alarms are blocked.
        }
    }
}
