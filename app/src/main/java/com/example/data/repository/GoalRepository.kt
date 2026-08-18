package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.GeminiMotivatorService
import com.example.data.db.AppDatabase
import com.example.data.db.GoalDao
import com.example.data.model.GoalEntity
import com.example.data.model.MotivationContent
import com.example.data.offline.OfflineInspirations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GoalRepository(
    private val context: Context,
    private val goalDao: GoalDao = AppDatabase.getInstance(context).goalDao(),
    private val geminiService: GeminiMotivatorService = GeminiMotivatorService()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_motivator_prefs", Context.MODE_PRIVATE)

    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    val activeGoals: Flow<List<GoalEntity>> = goalDao.getActiveGoals()
    val achievedGoals: Flow<List<GoalEntity>> = goalDao.getAchievedGoals()
    val activeGoalCount: Flow<Int> = goalDao.getActiveGoalCount()

    // SharedPreferences getters/setters for frequency and settings
    var frequencyPerDay: Int
        get() = prefs.getInt("frequency_per_day", 3).coerceIn(1, 12)
        set(value) = prefs.edit().putInt("frequency_per_day", value.coerceIn(1, 12)).apply()

    var idleNudgesEnabled: Boolean
        get() = prefs.getBoolean("idle_nudges_enabled", true)
        set(value) = prefs.edit().putBoolean("idle_nudges_enabled", value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    var totalNudgesDelivered: Int
        get() = prefs.getInt("total_nudges_delivered", 0)
        set(value) = prefs.edit().putInt("total_nudges_delivered", value).apply()

    var respectPoints: Int
        get() = prefs.getInt("respect_points", 100)
        set(value) = prefs.edit().putInt("respect_points", value).apply()

    suspend fun addGoal(
        title: String,
        category: String,
        whyItMatters: String,
        frequency: Int = 3
    ): Result<Long> {
        val currentActiveCount = goalDao.getActiveGoalCount().first()
        if (currentActiveCount >= 5) {
            return Result.failure(IllegalStateException("Maximum of 5 active goals reached. Achieve or remove a goal to add another."))
        }

        // Fetch initial inspiration
        val initialInspiration = try {
            geminiService.generateInspirationForGoal(title, category, whyItMatters)
        } catch (e: Exception) {
            OfflineInspirations.getInspirationForGoal(title, category)
        }

        val goal = GoalEntity(
            title = title.trim(),
            category = category,
            whyItMatters = whyItMatters.trim(),
            targetFrequencyPerDay = frequency,
            cachedQuote = initialInspiration.quote,
            cachedAuthor = initialInspiration.author,
            cachedStory = initialInspiration.story,
            cachedTakeaway = initialInspiration.takeaway,
            cachedActionNudge = initialInspiration.actionNudge,
            lastNudgeTimestamp = System.currentTimeMillis(),
            nudgeCount = 1
        )

        val newId = goalDao.insertGoal(goal)
        return Result.success(newId)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    suspend fun markGoalAchieved(goal: GoalEntity): String {
        goalDao.markAsAchieved(goal.id)
        respectPoints += 250
        return geminiService.generateMissionPassedDebrief(goal.title, goal.category)
    }

    suspend fun unmarkGoalAchieved(goalId: Long) {
        goalDao.unmarkAchieved(goalId)
    }

    suspend fun refreshMotivationForGoal(goal: GoalEntity, isIdle: Boolean = false): MotivationContent {
        val inspiration = geminiService.generateInspirationForGoal(
            goalTitle = goal.title,
            category = goal.category,
            whyItMatters = goal.whyItMatters,
            isIdleSlackingNudge = isIdle
        )

        goalDao.updateCachedInspiration(
            id = goal.id,
            quote = inspiration.quote,
            author = inspiration.author,
            story = inspiration.story,
            takeaway = inspiration.takeaway,
            nudge = inspiration.actionNudge,
            timestamp = System.currentTimeMillis()
        )

        totalNudgesDelivered += 1
        return inspiration
    }

    suspend fun getNextGoalForPeriodicNudge(isIdle: Boolean = false): Pair<GoalEntity, MotivationContent>? {
        val active = goalDao.getActiveGoalsSnapshot()
        if (active.isEmpty()) return null

        val selectedGoal = active.random()
        val inspiration = geminiService.generateInspirationForGoal(
            goalTitle = selectedGoal.title,
            category = selectedGoal.category,
            whyItMatters = selectedGoal.whyItMatters,
            isIdleSlackingNudge = isIdle
        )

        goalDao.updateCachedInspiration(
            id = selectedGoal.id,
            quote = inspiration.quote,
            author = inspiration.author,
            story = inspiration.story,
            takeaway = inspiration.takeaway,
            nudge = inspiration.actionNudge,
            timestamp = System.currentTimeMillis()
        )

        totalNudgesDelivered += 1
        return Pair(selectedGoal, inspiration)
    }

    suspend fun getGoalById(id: Long): GoalEntity? = goalDao.getGoalById(id)
}
