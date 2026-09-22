package com.pocketbudget.app

import com.pocketbudget.app.domain.model.Goal
import com.pocketbudget.app.utils.GoalEstimator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoalEstimatorTest {

    @Test
    fun testEstimateTimeToGoalPromptExample() {
        val goal = Goal(
            id = "samsung_a55",
            name = "Samsung A55",
            targetAmount = 4500000.0,
            currentAmount = 346000.0
        )
        // Average saving: 120.000/week -> Estimated ≈ 35 weeks
        val result = GoalEstimator.estimateTimeToGoal(goal, averageWeeklySavings = 120000.0)
        assertEquals("≈ 35 weeks", result)
    }

    @Test
    fun testEstimateWhenDataInsufficient() {
        val goal = Goal(
            id = "goal_no_data",
            name = "Goal With No Savings",
            targetAmount = 1000000.0,
            currentAmount = 0.0
        )
        // If average saving is 0 or negative, don't show false estimation
        val resultZero = GoalEstimator.estimateTimeToGoal(goal, averageWeeklySavings = 0.0)
        assertNull(resultZero)

        val resultNegative = GoalEstimator.estimateTimeToGoal(goal, averageWeeklySavings = -50000.0)
        assertNull(resultNegative)
    }

    @Test
    fun testEstimateWhenCompleted() {
        val goal = Goal(
            id = "goal_done",
            name = "Completed Goal",
            targetAmount = 1000000.0,
            currentAmount = 1000000.0
        )
        val result = GoalEstimator.estimateTimeToGoal(goal, averageWeeklySavings = 100000.0)
        assertEquals("Goal reached!", result)
    }
}
