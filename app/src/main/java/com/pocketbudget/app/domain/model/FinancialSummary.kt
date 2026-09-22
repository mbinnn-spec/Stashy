package com.pocketbudget.app.domain.model

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0
) {
    val currentBalance: Double
        get() = totalIncome - totalExpense

    val monthlyNet: Double
        get() = monthlyIncome - monthlyExpense
}
