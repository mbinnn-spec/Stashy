package com.pocketbudget.app.data.repository

import com.pocketbudget.app.data.local.dao.GoalDao
import com.pocketbudget.app.data.local.entity.GoalEntity
import com.pocketbudget.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepository(
    private val goalDao: GoalDao
) {
    fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTopGoal(): Flow<Goal?> {
        return goalDao.getTopGoal().map { it?.toDomain() }
    }

    fun getGoalById(id: String): Flow<Goal?> {
        return goalDao.getGoalById(id).map { it?.toDomain() }
    }

    suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(GoalEntity.fromDomain(goal))
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(GoalEntity.fromDomain(goal))
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(GoalEntity.fromDomain(goal))
    }

    suspend fun deleteGoalById(id: String) {
        goalDao.deleteGoalById(id)
    }

    suspend fun addContribution(id: String, amount: Double) {
        goalDao.addContribution(id, amount)
    }

    suspend fun deleteAllGoals() {
        goalDao.deleteAllGoals()
    }
}
