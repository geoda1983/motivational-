package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.repository.GoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MotivationAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_SEND_MOTIVATION = "com.example.ACTION_SEND_MOTIVATION"
        const val ACTION_IDLE_NUDGE = "com.example.ACTION_IDLE_NUDGE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("MotivationAlarmReceiver", "Received action: $action")

        val repository = GoalRepository(context)

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            MotivationScheduler.scheduleAlarms(
                context,
                repository.frequencyPerDay,
                repository.idleNudgesEnabled
            )
            return
        }

        val isIdle = (action == ACTION_IDLE_NUDGE)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nextNudge = repository.getNextGoalForPeriodicNudge(isIdle = isIdle)
                if (nextNudge != null) {
                    val (goal, inspiration) = nextNudge
                    NotificationHelper.showMotivationNotification(
                        context = context,
                        goal = goal,
                        content = inspiration,
                        isIdleNudge = isIdle
                    )
                }

                // Reschedule future alarms to keep chain alive
                MotivationScheduler.scheduleAlarms(
                    context,
                    repository.frequencyPerDay,
                    repository.idleNudgesEnabled
                )
            } catch (e: Exception) {
                Log.e("MotivationAlarmReceiver", "Error delivering notification", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
