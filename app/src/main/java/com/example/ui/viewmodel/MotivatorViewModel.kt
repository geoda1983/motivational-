package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GoalCategory
import com.example.data.model.GoalEntity
import com.example.data.model.MotivationContent
import com.example.data.repository.GoalRepository
import com.example.receiver.MotivationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MissionPassedState(
    val goal: GoalEntity,
    val debriefMessage: String,
    val respectEarned: Int = 250
)

data class MotivatorUiState(
    val activeGoals: List<GoalEntity> = emptyList(),
    val achievedGoals: List<GoalEntity> = emptyList(),
    val activeGoalCount: Int = 0,
    val frequencyPerDay: Int = 3,
    val idleNudgesEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val totalNudgesDelivered: Int = 0,
    val respectPoints: Int = 100,
    val isLoadingAi: Boolean = false,
    val missionPassedCelebration: MissionPassedState? = null,
    val userMessage: String? = null
)

class MotivatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalRepository(application)
    private val context = application.applicationContext

    private val _isLoadingAi = MutableStateFlow(false)
    private val _missionPassedCelebration = MutableStateFlow<MissionPassedState?>(null)
    private val _userMessage = MutableStateFlow<String?>(null)
    private val _frequency = MutableStateFlow(repository.frequencyPerDay)
    private val _idleEnabled = MutableStateFlow(repository.idleNudgesEnabled)
    private val _notifsEnabled = MutableStateFlow(repository.notificationsEnabled)
    private val _respectPoints = MutableStateFlow(repository.respectPoints)
    private val _totalNudges = MutableStateFlow(repository.totalNudgesDelivered)

    val uiState: StateFlow<MotivatorUiState> = combine(
        repository.activeGoals,
        repository.achievedGoals,
        _isLoadingAi,
        _missionPassedCelebration,
        _userMessage,
        _frequency,
        _idleEnabled,
        _notifsEnabled,
        _respectPoints,
        _totalNudges
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val active = args[0] as List<GoalEntity>
        @Suppress("UNCHECKED_CAST")
        val achieved = args[1] as List<GoalEntity>
        val loading = args[2] as Boolean
        val celebration = args[3] as MissionPassedState?
        val msg = args[4] as String?
        val freq = args[5] as Int
        val idle = args[6] as Boolean
        val notifs = args[7] as Boolean
        val respect = args[8] as Int
        val total = args[9] as Int

        MotivatorUiState(
            activeGoals = active,
            achievedGoals = achieved,
            activeGoalCount = active.size,
            frequencyPerDay = freq,
            idleNudgesEnabled = idle,
            notificationsEnabled = notifs,
            totalNudgesDelivered = total,
            respectPoints = respect,
            isLoadingAi = loading,
            missionPassedCelebration = celebration,
            userMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MotivatorUiState()
    )

    init {
        // Schedule alarms on startup
        MotivationScheduler.scheduleAlarms(
            context,
            repository.frequencyPerDay,
            repository.idleNudgesEnabled
        )

        // Seed default starter goals if first time launch and database is empty
        viewModelScope.launch {
            repository.allGoals.collect { list ->
                if (list.isEmpty()) {
                    seedStarterGoals()
                }
            }
        }
    }

    private suspend fun seedStarterGoals() {
        repository.addGoal(
            title = "Read 20 pages of high-impact non-fiction daily",
            category = GoalCategory.LEARNING.displayName,
            whyItMatters = "Expand cognitive range, learn from top minds, and build mental sharpness."
        )
        repository.addGoal(
            title = "Crush 45 minutes of intense strength & cardio training",
            category = GoalCategory.FITNESS.displayName,
            whyItMatters = "Maximize physical stamina, mental clarity, and longevity."
        )
    }

    fun addNewGoal(title: String, category: String, whyItMatters: String) {
        if (title.isBlank()) {
            _userMessage.value = "Please enter your goal statement."
            return
        }

        viewModelScope.launch {
            _isLoadingAi.value = true
            val result = repository.addGoal(
                title = title,
                category = category,
                whyItMatters = whyItMatters,
                frequency = _frequency.value
            )
            _isLoadingAi.value = false

            result.onSuccess {
                _userMessage.value = "🎯 Goal created with AI-tailored motivation!"
                MotivationScheduler.scheduleAlarms(
                    context,
                    _frequency.value,
                    _idleEnabled.value
                )
            }.onFailure { error ->
                _userMessage.value = error.message ?: "Failed to add goal."
            }
        }
    }

    fun updateGoalFrequency(newFrequency: Int) {
        val clamped = newFrequency.coerceIn(1, 12)
        repository.frequencyPerDay = clamped
        _frequency.value = clamped
        MotivationScheduler.scheduleAlarms(context, clamped, _idleEnabled.value)
        _userMessage.value = "Notification frequency updated to $clamped per day."
    }

    fun toggleIdleNudges(enabled: Boolean) {
        repository.idleNudgesEnabled = enabled
        _idleEnabled.value = enabled
        MotivationScheduler.scheduleAlarms(context, _frequency.value, enabled)
        _userMessage.value = if (enabled) "Idle & slump alerts enabled." else "Idle alerts paused."
    }

    fun toggleNotifications(enabled: Boolean) {
        repository.notificationsEnabled = enabled
        _notifsEnabled.value = enabled
        if (enabled) {
            MotivationScheduler.scheduleAlarms(context, _frequency.value, _idleEnabled.value)
            _userMessage.value = "Motivational notifications enabled."
        } else {
            _userMessage.value = "Notifications paused."
        }
    }

    fun refreshGoalMotivation(goal: GoalEntity, isIdle: Boolean = false) {
        viewModelScope.launch {
            _isLoadingAi.value = true
            try {
                repository.refreshMotivationForGoal(goal, isIdle)
                _totalNudges.value = repository.totalNudgesDelivered
                _userMessage.value = "✨ Fresh AI quote & inspiration generated!"
            } catch (e: Exception) {
                _userMessage.value = "Inspiration updated."
            } finally {
                _isLoadingAi.value = false
            }
        }
    }

    fun markGoalAsAchieved(goal: GoalEntity) {
        viewModelScope.launch {
            _isLoadingAi.value = true
            val debrief = repository.markGoalAchieved(goal)
            _respectPoints.value = repository.respectPoints
            _isLoadingAi.value = false

            // Trigger "MISSION PASSED" Celebration
            _missionPassedCelebration.value = MissionPassedState(
                goal = goal,
                debriefMessage = debrief,
                respectEarned = 250
            )
        }
    }

    fun unmarkGoalAchieved(goalId: Long) {
        viewModelScope.launch {
            repository.unmarkGoalAchieved(goalId)
            _userMessage.value = "Goal returned to active list."
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            _userMessage.value = "Goal deleted."
        }
    }

    fun triggerTestNotification(isIdle: Boolean = false) {
        MotivationScheduler.triggerImmediateTestNudge(context, isIdle)
        _userMessage.value = if (isIdle) "⚡ Anti-slacking alert sent to notification bar!" else "🎯 Goal motivation sent to notification bar!"
    }

    fun dismissCelebration() {
        _missionPassedCelebration.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
