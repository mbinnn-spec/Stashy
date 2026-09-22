package com.pocketbudget.app.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pocketbudget.app.data.repository.GoalRepository
import com.pocketbudget.app.data.repository.TransactionRepository
import com.pocketbudget.app.domain.model.Goal
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
import java.util.UUID

data class GoalFormState(
    val id: String? = null,
    val name: String = "",
    val targetAmountDigits: String = "",
    val currentAmountDigits: String = "",
    val deadline: Long? = null,
    val nameError: String? = null,
    val targetError: String? = null
)

class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // All savings goals
    val goals: StateFlow<List<Goal>> = goalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly savings average calculated from user cashflow
    val weeklySavingAverage: StateFlow<Double> = combine(
        transactionRepository.getTotalIncome(),
        transactionRepository.getTotalExpense()
    ) { totalInc, totalExp ->
        val net = totalInc - totalExp
        // Assume rough 4-week window or positive net rate
        if (net > 0) net / 4.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Form State
    private val _formState = MutableStateFlow(GoalFormState())
    val formState: StateFlow<GoalFormState> = _formState.asStateFlow()

    // Contribution State
    private val _contributingGoal = MutableStateFlow<Goal?>(null)
    val contributingGoal: StateFlow<Goal?> = _contributingGoal.asStateFlow()

    private val _contributionDigits = MutableStateFlow("")
    val contributionDigits: StateFlow<String> = _contributionDigits.asStateFlow()

    private val _contributionError = MutableStateFlow<String?>(null)
    val contributionError: StateFlow<String?> = _contributionError.asStateFlow()

    // Delete State
    private val _deletingGoal = MutableStateFlow<Goal?>(null)
    val deletingGoal: StateFlow<Goal?> = _deletingGoal.asStateFlow()

    // Events
    private val _dismissSheetEvent = MutableSharedFlow<Unit>()
    val dismissSheetEvent: SharedFlow<Unit> = _dismissSheetEvent.asSharedFlow()

    fun startAddGoal() {
        _formState.value = GoalFormState(
            id = null,
            name = "",
            targetAmountDigits = "",
            currentAmountDigits = "",
            deadline = null
        )
    }

    fun startEditGoal(goal: Goal) {
        _formState.value = GoalFormState(
            id = goal.id,
            name = goal.name,
            targetAmountDigits = goal.targetAmount.toLong().toString(),
            currentAmountDigits = if (goal.currentAmount > 0) goal.currentAmount.toLong().toString() else "",
            deadline = goal.deadline
        )
    }

    fun onNameChanged(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun onTargetDigitsChanged(digits: String) {
        _formState.update { it.copy(targetAmountDigits = digits, targetError = null) }
    }

    fun onCurrentDigitsChanged(digits: String) {
        _formState.update { it.copy(currentAmountDigits = digits) }
    }

    fun onDeadlineChanged(deadline: Long?) {
        _formState.update { it.copy(deadline = deadline) }
    }

    fun saveGoal() {
        val state = _formState.value
        val name = state.name.trim()
        val target = state.targetAmountDigits.toDoubleOrNull() ?: 0.0
        val current = state.currentAmountDigits.toDoubleOrNull() ?: 0.0

        var hasError = false
        if (name.isBlank()) {
            _formState.update { it.copy(nameError = "Goal name is required") }
            hasError = true
        }

        if (target <= 0) {
            _formState.update { it.copy(targetError = "Target amount must be greater than 0") }
            hasError = true
        }

        if (hasError) return

        val goal = Goal(
            id = state.id ?: UUID.randomUUID().toString(),
            name = name,
            targetAmount = target,
            currentAmount = current,
            deadline = state.deadline,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            if (state.id == null) {
                goalRepository.insertGoal(goal)
            } else {
                goalRepository.updateGoal(goal)
            }
            _dismissSheetEvent.emit(Unit)
        }
    }

    // Contribution Actions
    fun openContribution(goal: Goal) {
        _contributingGoal.value = goal
        _contributionDigits.value = ""
        _contributionError.value = null
    }

    fun closeContribution() {
        _contributingGoal.value = null
        _contributionDigits.value = ""
        _contributionError.value = null
    }

    fun onContributionDigitsChanged(digits: String) {
        _contributionDigits.value = digits
        _contributionError.value = null
    }

    fun submitContribution() {
        val goal = _contributingGoal.value ?: return
        val amount = _contributionDigits.value.toDoubleOrNull() ?: 0.0

        if (amount <= 0) {
            _contributionError.value = "Amount must be greater than 0"
            return
        }

        viewModelScope.launch {
            goalRepository.addContribution(goal.id, amount)
            closeContribution()
        }
    }

    // Delete Actions
    fun requestDeleteGoal(goal: Goal) {
        _deletingGoal.value = goal
    }

    fun dismissDeleteGoal() {
        _deletingGoal.value = null
    }

    fun confirmDeleteGoal() {
        val goal = _deletingGoal.value ?: return
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
            dismissDeleteGoal()
        }
    }

    class Factory(
        private val goalRepository: GoalRepository,
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GoalViewModel(goalRepository, transactionRepository) as T
        }
    }
}
