package com.pocketbudget.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pocketbudget.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY (current_amount >= target_amount) ASC, created_at DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals ORDER BY (current_amount >= target_amount) ASC, created_at DESC LIMIT 1")
    fun getTopGoal(): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    fun getGoalById(id: String): Flow<GoalEntity?>

    @Query("SELECT * FROM goals ORDER BY (current_amount >= target_amount) ASC, created_at DESC")
    suspend fun getAllGoalsDirect(): List<GoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    @Query("UPDATE goals SET current_amount = current_amount + :amount WHERE id = :id")
    suspend fun addContribution(id: String, amount: Double)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}
