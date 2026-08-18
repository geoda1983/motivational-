package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.GoalEntity
import com.example.data.model.MotivationContent

object NotificationHelper {
    const val CHANNEL_ID_MOTIVATION = "channel_goal_motivation"
    const val CHANNEL_ID_IDLE = "channel_idle_slump_buster"
    const val NOTIFICATION_ID_BASE = 1001

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val motivationChannel = NotificationChannel(
                CHANNEL_ID_MOTIVATION,
                "Goal Motivations & Stories",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Periodic inspirational quotes and micro-stories tailored to your stated goals."
                enableLights(true)
                lightColor = Color.parseColor("#FFD54F")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250)
            }

            val idleChannel = NotificationChannel(
                CHANNEL_ID_IDLE,
                "Idle & Anti-Slacking Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Smart reminders when you are most idle or slacking off to regain momentum."
                enableLights(true)
                lightColor = Color.parseColor("#FF5252")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }

            notificationManager.createNotificationChannel(motivationChannel)
            notificationManager.createNotificationChannel(idleChannel)
        }
    }

    fun showMotivationNotification(
        context: Context,
        goal: GoalEntity,
        content: MotivationContent,
        isIdleNudge: Boolean = false
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_GOAL_ID", goal.id)
            putExtra("EXTRA_FROM_NOTIFICATION", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            goal.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isIdleNudge) CHANNEL_ID_IDLE else CHANNEL_ID_MOTIVATION
        val notificationTitle = if (isIdleNudge) {
            "⚡ Anti-Slacking Wakeup: ${goal.title}"
        } else {
            "🎯 Goal Fuel: ${goal.title}"
        }

        val bigText = buildString {
            append("\"${content.quote}\"\n— ${content.author}\n\n")
            if (content.story.isNotBlank()) {
                append("📖 Story: ${content.story}\n\n")
            }
            if (content.actionNudge.isNotBlank()) {
                append("🔥 Nudge: ${content.actionNudge}")
            }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.img_app_icon_1787027922609)
            .setContentTitle(notificationTitle)
            .setContentText("\"${content.quote}\" — ${content.author}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(notificationTitle)
                    .setSummaryText(if (isIdleNudge) "Idle Alert • Resume Momentum" else "AI Goal Motivator")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setColor(if (isIdleNudge) Color.parseColor("#FF5252") else Color.parseColor("#FFD54F"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.img_app_icon_1787027922609,
                "I'm on it! 🚀",
                pendingIntent
            )
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = NOTIFICATION_ID_BASE + (goal.id % 100).toInt()
        notificationManager.notify(notificationId, notification)
    }
}
