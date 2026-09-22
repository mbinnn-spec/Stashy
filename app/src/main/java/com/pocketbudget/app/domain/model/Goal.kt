package com.pocketbudget.app.domain.model

data class Goal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Progress percentage calculated dynamically:
     * currentAmount / targetAmount * 100
     */
    val progressPercentage: Double
        get() = if (targetAmount > 0) {
            ((currentAmount / targetAmount) * 100).coerceIn(0.0, 100.0)
        } else {
            0.0
        }

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}
