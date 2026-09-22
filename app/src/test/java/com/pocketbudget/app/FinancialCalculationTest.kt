package com.pocketbudget.app

import com.pocketbudget.app.domain.model.FinancialSummary
import com.pocketbudget.app.domain.model.Goal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculationTest {

    @Test
    fun testBalanceCalculation() {
        val summary = FinancialSummary(
            totalIncome = 480000.0,
            totalExpense = 214000.0,
            monthlyIncome = 480000.0,
            monthlyExpense = 214000.0
        )
        // Balance = Total Income - Total Expense
        assertEquals(266000.0, summary.currentBalance, 0.001)
        assertEquals(266000.0, summary.monthlyNet, 0.001)
    }

    @Test
    fun testGoalProgressCalculation() {
        val goal = Goal(
            id = "samsung_a55",
            name = "Samsung A55",
            targetAmount = 4500000.0,
            currentAmount = 346000.0
        )
        // 346000 / 4500000 * 100 ≈ 7.6888... %
        assertEquals(7.688, goal.progressPercentage, 0.01)
        assertEquals(4154000.0, goal.remainingAmount, 0.001)
    }

    @Test
    fun testGoalCompletion() {
        val goal = Goal(
            id = "test_goal",
            name = "Completed Goal",
            targetAmount = 1000000.0,
            currentAmount = 1000000.0
        )
        assertEquals(100.0, goal.progressPercentage, 0.001)
        assertEquals(0.0, goal.remainingAmount, 0.001)
        assertTrue(goal.isCompleted)
    }
}
