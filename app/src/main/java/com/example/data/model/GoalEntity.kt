package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Growth",
    val whyItMatters: String = "",
    val targetFrequencyPerDay: Int = 3,
    val isAchieved: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val achievedTimestamp: Long? = null,
    val cachedQuote: String? = null,
    val cachedAuthor: String? = null,
    val cachedStory: String? = null,
    val cachedTakeaway: String? = null,
    val cachedActionNudge: String? = null,
    val lastNudgeTimestamp: Long? = null,
    val nudgeCount: Int = 0
)
