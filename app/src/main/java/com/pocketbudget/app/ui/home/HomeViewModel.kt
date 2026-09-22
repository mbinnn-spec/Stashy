package com.pocketbudget.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pocketbudget.app.data.repository.GoalRepository
import com.pocketbudget.app.data.repository.TransactionRepository
import com.pocketbudget.app.domain.model.CategoryExpenseSummary
import com.pocketbudget.app.domain.model.FinancialSummary
import com.pocketbudget.app.domain.model.Goal
import com.pocketbudget.app.domain.model.Transaction
import com.pocketbudget.app.domain.model.TransactionType
import com.pocketbudget.app.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: FinancialSummary = FinancialSummary(),
    val recentTransactions: List<Transaction> = emptyList(),
    val topGoal: Goal? = null,
    val categoryBreakdown: List<CategoryExpenseSummary> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val startOfMonth = DateUtils.getStartOfMonth()
    private val endOfMonth = DateUtils.getEndOfMonth()

    // Combine financial summary flows (4 flows)
    private val financialSummaryFlow: Flow<FinancialSummary> = combine(
        transactionRepository.getTotalIncome(),
        transactionRepository.getTotalExpense(),
        transactionRepository.getIncomeBetweenDates(startOfMonth, endOfMonth),
        transactionRepository.getExpenseBetweenDates(startOfMonth, endOfMonth)
    ) { totalInc, totalExp, monthInc, monthExp ->
        FinancialSummary(
            totalIncome = totalInc,
            totalExpense = totalExp,
            monthlyIncome = monthInc,
            monthlyExpense = monthExp
        )
    }

    // Monthly category breakdown flow
    private val categoryBreakdownFlow: Flow<List<CategoryExpenseSummary>> =
        transactionRepository.getAllTransactions().combine(kotlinx.coroutines.flow.flowOf(Unit)) { transactions, _ ->
            val thisMonthExpenses = transactions.filter {
                it.type == TransactionType.EXPENSE && it.date in startOfMonth..endOfMonth
            }
            val totalExpense = thisMonthExpenses.sumOf { it.amount }

            if (totalExpense <= 0) {
                emptyList()
            } else {
                thisMonthExpenses
                    .groupBy { it.categoryId }
                    .map { (catId, txList) ->
                        val catName = txList.first().categoryName
                        val amount = txList.sumOf { it.amount }
                        val percentage = (amount / totalExpense) * 100.0
                        CategoryExpenseSummary(
                            categoryId = catId,
                            categoryName = catName,
                            totalAmount = amount,
                            percentage = percentage
                        )
                    }
                    .sortedByDescending { it.totalAmount }
            }
        }

    // Combine summary, recent transactions, top goal and category breakdown
    val uiState: StateFlow<HomeUiState> = combine(
        financialSummaryFlow,
        transactionRepository.getRecentTransactions(limit = 5),
        goalRepository.getTopGoal(),
        categoryBreakdownFlow
    ) { summary, recentTxs, topGoal, breakdown ->
        HomeUiState(
            summary = summary,
            recentTransactions = recentTxs,
            topGoal = topGoal,
            categoryBreakdown = breakdown,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState()
    )

    class Factory(
        private val transactionRepository: TransactionRepository,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(transactionRepository, goalRepository) as T
        }
    }
}
