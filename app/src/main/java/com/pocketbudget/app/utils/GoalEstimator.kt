package com.pocketbudget.app.utils

import com.pocketbudget.app.domain.model.Goal
import kotlin.math.ceil

object GoalEstimator {

    /**
     * Calculates estimated time to reach a savings goal based on average weekly savings.
     *
     * Example from specification:
     * Target: Rp 4.500.000
     * Current: Rp 346.000 (Remaining: Rp 4.154.000)
     * Average saving: Rp 120.000/week
     * Estimated: ≈ 35 weeks
     *
     * Rules:
     * - If target is already reached (remaining <= 0), returns "Goal reached!"
     * - If averageWeeklySavings <= 0 or data is insufficient, returns null
     *   (per rule: "Jika data belum cukup, jangan menampilkan estimasi palsu").
     */
    fun estimateTimeToGoal(goal: Goal, averageWeeklySavings: Double): String? {
        if (goal.isCompleted || goal.remainingAmount <= 0) {
            return "Goal reached!"
        }

        // Need positive weekly savings to calculate an honest estimation
        if (averageWeeklySavings <= 1000.0) {
            return null
        }

        val weeksNeeded = ceil(goal.remainingAmount / averageWeeklySavings).toInt()
        if (weeksNeeded <= 0) return null

        return when {
            weeksNeeded == 1 -> "≈ 1 week"
            weeksNeeded <= 100 -> "≈ $weeksNeeded weeks"
            else -> {
                val years = ceil(weeksNeeded / 52.0).toInt()
                if (years <= 1) "≈ 1 year" else "≈ $years years"
            }
        }
    }
}
