package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY isAchieved ASC, createdTimestamp DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isAchieved = 0 ORDER BY createdTimestamp DESC")
    fun getActiveGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isAchieved = 1 ORDER BY achievedTimestamp DESC")
    fun getAchievedGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isAchieved = 0")
    suspend fun getActiveGoalsSnapshot(): List<GoalEntity>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Query("SELECT COUNT(*) FROM goals WHERE isAchieved = 0")
    fun getActiveGoalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("UPDATE goals SET isAchieved = 1, achievedTimestamp = :timestamp WHERE id = :id")
    suspend fun markAsAchieved(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE goals SET isAchieved = 0, achievedTimestamp = NULL WHERE id = :id")
    suspend fun unmarkAchieved(id: Long)

    @Query("UPDATE goals SET cachedQuote = :quote, cachedAuthor = :author, cachedStory = :story, cachedTakeaway = :takeaway, cachedActionNudge = :nudge, lastNudgeTimestamp = :timestamp, nudgeCount = nudgeCount + 1 WHERE id = :id")
    suspend fun updateCachedInspiration(
        id: Long,
        quote: String,
        author: String,
        story: String,
        takeaway: String,
        nudge: String,
        timestamp: Long = System.currentTimeMillis()
    )
}
