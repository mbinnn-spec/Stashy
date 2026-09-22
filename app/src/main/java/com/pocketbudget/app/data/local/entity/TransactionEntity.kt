package com.pocketbudget.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pocketbudget.app.domain.model.Transaction
import com.pocketbudget.app.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["date"]),
        Index(value = ["type"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "category_name")
    val categoryName: String,
    val note: String? = null,
    val date: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        categoryId = categoryId,
        categoryName = categoryName,
        note = note,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity = TransactionEntity(
            id = transaction.id,
            type = transaction.type.name,
            amount = transaction.amount,
            categoryId = transaction.categoryId,
            categoryName = transaction.categoryName,
            note = transaction.note,
            date = transaction.date,
            createdAt = transaction.createdAt
        )
    }
}
