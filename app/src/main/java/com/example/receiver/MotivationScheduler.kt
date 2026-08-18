package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object MotivationScheduler {
    const val REQUEST_CODE_PERIODIC = 2001
    const val REQUEST_CODE_IDLE = 2002

    fun scheduleAlarms(context: Context, frequencyPerDay: Int, idleNudgesEnabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate intervals during active waking hours (8:00 AM to 10:00 PM = 14 active hours)
        val safeFrequency = frequencyPerDay.coerceIn(1, 12)
        val activeHoursMinutes = 14 * 60
        val intervalMinutes = (activeHoursMinutes / safeFrequency).coerceAtLeast(30)

        // Set next periodic alarm
        val periodicIntent = Intent(context, MotivationAlarmReceiver::class.java).apply {
            action = MotivationAlarmReceiver.ACTION_SEND_MOTIVATION
        }
        val periodicPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PERIODIC,
            periodicIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule periodic alarm (starting 1 interval from now, or at minimum 10 seconds for initial test)
        val triggerTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    periodicPendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    periodicPendingIntent
                )
            }
            Log.d("MotivationScheduler", "Scheduled periodic alarm in $intervalMinutes minutes ($safeFrequency times/day)")
        } catch (e: SecurityException) {
            Log.e("MotivationScheduler", "Failed to schedule exact alarm", e)
        }

        // Schedule Idle Slump Nudge (e.g., target 2:30 PM to 4:00 PM afternoon slump)
        if (idleNudgesEnabled) {
            val idleIntent = Intent(context, MotivationAlarmReceiver::class.java).apply {
                action = MotivationAlarmReceiver.ACTION_IDLE_NUDGE
            }
            val idlePendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_IDLE,
                idleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val idleCalendar = Calendar.getInstance().apply {
                val currentHour = get(Calendar.HOUR_OF_DAY)
                if (currentHour >= 15) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                set(Calendar.HOUR_OF_DAY, 14) // 2:30 PM typical slump time
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        idleCalendar.timeInMillis,
                        idlePendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        idleCalendar.timeInMillis,
                        idlePendingIntent
                    )
                }
            } catch (e: SecurityException) {
                Log.e("MotivationScheduler", "Failed to schedule idle alarm", e)
            }
        }
    }

    fun triggerImmediateTestNudge(context: Context, isIdle: Boolean = false) {
        val intent = Intent(context, MotivationAlarmReceiver::class.java).apply {
            action = if (isIdle) MotivationAlarmReceiver.ACTION_IDLE_NUDGE else MotivationAlarmReceiver.ACTION_SEND_MOTIVATION
        }
        context.sendBroadcast(intent)
    }
}
