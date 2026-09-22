package com.pocketbudget.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pocketbudget.app.domain.model.Goal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "target_amount")
    val targetAmount: Double,
    @ColumnInfo(name = "current_amount")
    val currentAmount: Double = 0.0,
    val deadline: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Goal = Goal(
        id = id,
        name = name,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(goal: Goal): GoalEntity = GoalEntity(
            id = goal.id,
            name = goal.name,
            targetAmount = goal.targetAmount,
            currentAmount = goal.currentAmount,
            deadline = goal.deadline,
            createdAt = goal.createdAt
        )
    }
}
