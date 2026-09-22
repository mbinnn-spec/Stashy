package com.pocketbudget.app.domain.model

data class CategoryExpenseSummary(
    val categoryId: String,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Double
)
