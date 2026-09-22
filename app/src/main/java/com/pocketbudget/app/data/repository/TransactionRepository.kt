package com.pocketbudget.app.data.repository

import com.pocketbudget.app.data.local.dao.TransactionDao
import com.pocketbudget.app.data.local.entity.TransactionEntity
import com.pocketbudget.app.domain.model.Transaction
import com.pocketbudget.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getRecentTransactions(limit: Int = 5): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTransactionById(id: String): Flow<Transaction?> {
        return transactionDao.getTransactionById(id).map { it?.toDomain() }
    }

    suspend fun getTransactionByIdDirect(id: String): Transaction? {
        return transactionDao.getTransactionByIdDirect(id)?.toDomain()
    }

    fun getTotalIncome(): Flow<Double> {
        return transactionDao.getTotalIncome()
    }

    fun getTotalExpense(): Flow<Double> {
        return transactionDao.getTotalExpense()
    }

    fun getIncomeBetweenDates(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getIncomeBetweenDates(startDate, endDate)
    }

    fun getExpenseBetweenDates(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getExpenseBetweenDates(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(TransactionEntity.fromDomain(transaction))
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(TransactionEntity.fromDomain(transaction))
    }

    suspend fun deleteTransactionById(id: String) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
}
