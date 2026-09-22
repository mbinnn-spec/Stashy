package com.pocketbudget.app.domain.model

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val categoryId: String,
    val categoryName: String,
    val note: String? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
