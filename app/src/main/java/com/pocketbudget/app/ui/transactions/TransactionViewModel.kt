package com.pocketbudget.app.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pocketbudget.app.data.repository.CategoryRepository
import com.pocketbudget.app.data.repository.TransactionRepository
import com.pocketbudget.app.domain.model.Category
import com.pocketbudget.app.domain.model.Transaction
import com.pocketbudget.app.domain.model.TransactionType
import com.pocketbudget.app.utils.DateUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

enum class DateFilterOption(val label: String) {
    ALL("All Time"),
    THIS_MONTH("This Month"),
    LAST_30_DAYS("30 Days"),
    TODAY("Today")
}

data class TransactionFormState(
    val id: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountDigits: String = "",
    val selectedCategory: Category? = null,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val amountError: String? = null,
    val categoryError: String? = null
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Category lists by type
    val incomeCategories: StateFlow<List<Category>> = categoryRepository
        .getCategoriesByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<Category>> = categoryRepository
        .getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Form State
    private val _formState = MutableStateFlow(TransactionFormState())
    val formState: StateFlow<TransactionFormState> = _formState.asStateFlow()

    // Navigation events (e.g. pop back on save)
    private val _saveSuccessEvent = MutableSharedFlow<Unit>()
    val saveSuccessEvent: SharedFlow<Unit> = _saveSuccessEvent.asSharedFlow()

    // Detail & Delete Dialog state
    private val _detailTransaction = MutableStateFlow<Transaction?>(null)
    val detailTransaction: StateFlow<Transaction?> = _detailTransaction.asStateFlow()

    private val _showDeleteConfirm = MutableStateFlow(false)
    val showDeleteConfirm: StateFlow<Boolean> = _showDeleteConfirm.asStateFlow()

    // History filter states
    private val _filterType = MutableStateFlow<TransactionType?>(null)
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    private val _filterCategoryId = MutableStateFlow<String?>(null)
    val filterCategoryId: StateFlow<String?> = _filterCategoryId.asStateFlow()

    private val _dateFilter = MutableStateFlow(DateFilterOption.ALL)
    val dateFilter: StateFlow<DateFilterOption> = _dateFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Available categories based on current type filter
    val availableFilterCategories: StateFlow<List<Category>> = combine(
        allCategories,
        _filterType
    ) { categories, type ->
        if (type == null) categories else categories.filter { it.type == type }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered transaction list combining all 5 criteria
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactionRepository.getAllTransactions(),
        _filterType,
        _filterCategoryId,
        _dateFilter,
        _searchQuery
    ) { transactions, type, catId, dateOpt, query ->
        val now = System.currentTimeMillis()
        val startOfMonth = DateUtils.getStartOfMonth()
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)

        transactions.filter { tx ->
            val matchesType = type == null || tx.type == type
            val matchesCategory = catId == null || tx.categoryId == catId
            val matchesDate = when (dateOpt) {
                DateFilterOption.ALL -> true
                DateFilterOption.TODAY -> tx.date >= startOfToday
                DateFilterOption.THIS_MONTH -> tx.date >= startOfMonth
                DateFilterOption.LAST_30_DAYS -> tx.date >= thirtyDaysAgo
            }
            val matchesQuery = query.isBlank() ||
                    tx.categoryName.contains(query, ignoreCase = true) ||
                    (tx.note?.contains(query, ignoreCase = true) == true)

            matchesType && matchesCategory && matchesDate && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Grouped by date for display
    val groupedTransactions: StateFlow<Map<String, List<Transaction>>> = filteredTransactions
        .combine(MutableStateFlow(Unit)) { txList, _ ->
            txList.groupBy { tx -> DateUtils.formatHeaderDate(tx.date) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Actions for Form
    fun startAddTransaction(initialType: TransactionType = TransactionType.EXPENSE) {
        val categories = if (initialType == TransactionType.INCOME) incomeCategories.value else expenseCategories.value
        _formState.value = TransactionFormState(
            id = null,
            type = initialType,
            amountDigits = "",
            selectedCategory = categories.firstOrNull(),
            note = "",
            date = System.currentTimeMillis()
        )
    }

    fun startEditTransaction(transaction: Transaction) {
        val categories = if (transaction.type == TransactionType.INCOME) incomeCategories.value else expenseCategories.value
        val matchedCategory = categories.find { it.id == transaction.categoryId }
            ?: Category(
                id = transaction.categoryId,
                name = transaction.categoryName,
                type = transaction.type,
                iconName = "Category",
                colorHex = if (transaction.type == TransactionType.INCOME) "#10B981" else "#F43F5E"
            )

        _formState.value = TransactionFormState(
            id = transaction.id,
            type = transaction.type,
            amountDigits = transaction.amount.toLong().toString(),
            selectedCategory = matchedCategory,
            note = transaction.note ?: "",
            date = transaction.date
        )
    }

    fun onTypeChanged(newType: TransactionType) {
        val categories = if (newType == TransactionType.INCOME) incomeCategories.value else expenseCategories.value
        _formState.update { current ->
            current.copy(
                type = newType,
                selectedCategory = categories.firstOrNull(),
                categoryError = null
            )
        }
    }

    fun onAmountDigitsChanged(digits: String) {
        _formState.update { it.copy(amountDigits = digits, amountError = null) }
    }

    fun onCategorySelected(category: Category) {
        _formState.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun onNoteChanged(note: String) {
        _formState.update { it.copy(note = note) }
    }

    fun onDateChanged(timestamp: Long) {
        _formState.update { it.copy(date = timestamp) }
    }

    fun saveTransaction() {
        val state = _formState.value
        val amount = state.amountDigits.toDoubleOrNull() ?: 0.0

        var hasError = false
        if (amount <= 0) {
            _formState.update { it.copy(amountError = "Amount must be greater than 0") }
            hasError = true
        }

        if (state.selectedCategory == null) {
            _formState.update { it.copy(categoryError = "Please select a category") }
            hasError = true
        }

        if (hasError) return

        val category = state.selectedCategory!!
        val transaction = Transaction(
            id = state.id ?: UUID.randomUUID().toString(),
            type = state.type,
            amount = amount,
            categoryId = category.id,
            categoryName = category.name,
            note = state.note.trim().ifEmpty { null },
            date = state.date,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            if (state.id == null) {
                transactionRepository.insertTransaction(transaction)
            } else {
                transactionRepository.updateTransaction(transaction)
            }
            _saveSuccessEvent.emit(Unit)
        }
    }

    // Detail & Delete Actions
    fun openDetail(transaction: Transaction) {
        _detailTransaction.value = transaction
    }

    fun closeDetail() {
        _detailTransaction.value = null
        _showDeleteConfirm.value = false
    }

    fun requestDeleteConfirmation() {
        _showDeleteConfirm.value = true
    }

    fun dismissDeleteConfirmation() {
        _showDeleteConfirm.value = false
    }

    fun deleteCurrentTransaction() {
        val tx = _detailTransaction.value ?: return
        viewModelScope.launch {
            transactionRepository.deleteTransaction(tx)
            closeDetail()
        }
    }

    // Filter Actions
    fun setFilterType(type: TransactionType?) {
        _filterType.value = type
        // If the selected category doesn't belong to the new type, reset it
        val currentCategory = _filterCategoryId.value
        if (currentCategory != null && type != null) {
            val cat = allCategories.value.find { it.id == currentCategory }
            if (cat != null && cat.type != type) {
                _filterCategoryId.value = null
            }
        }
    }

    fun setFilterCategory(categoryId: String?) {
        _filterCategoryId.value = categoryId
    }

    fun setDateFilter(option: DateFilterOption) {
        _dateFilter.value = option
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAllFilters() {
        _filterType.value = null
        _filterCategoryId.value = null
        _dateFilter.value = DateFilterOption.ALL
        _searchQuery.value = ""
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionViewModel(transactionRepository, categoryRepository) as T
        }
    }
}
