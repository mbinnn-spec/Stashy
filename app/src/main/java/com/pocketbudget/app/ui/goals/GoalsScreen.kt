package com.pocketbudget.app.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketbudget.app.ui.components.ConfirmationDialog
import com.pocketbudget.app.ui.components.EmptyStateView
import com.pocketbudget.app.ui.components.GoalCard
import com.pocketbudget.app.ui.theme.GoalAccent
import com.pocketbudget.app.utils.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalViewModel
) {
    val goals by viewModel.goals.collectAsState()
    val weeklySavingAverage by viewModel.weeklySavingAverage.collectAsState()
    val contributingGoal by viewModel.contributingGoal.collectAsState()
    val contributionDigits by viewModel.contributionDigits.collectAsState()
    val contributionError by viewModel.contributionError.collectAsState()
    val deletingGoal by viewModel.deletingGoal.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.dismissSheetEvent.collectLatest {
            coroutineScope.launch {
                sheetState.hide()
                showBottomSheet = false
            }
        }
    }

    val totalSaved = goals.sumOf { it.currentAmount }
    val totalTarget = goals.sumOf { it.targetAmount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startAddGoal()
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Create Goal",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Page Header
            Text(
                text = "Savings Goals",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (goals.isNotEmpty()) {
                // Total Savings Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Accumulated Savings",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatRupiah(totalSaved),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoalAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Target Needed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatRupiah(totalTarget),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Goals List or Empty State
            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = "No savings goals yet.",
                        description = "Create a goal and start tracking your progress.",
                        icon = Icons.Rounded.Flag,
                        actionButtonText = "Create Goal",
                        onActionClick = {
                            viewModel.startAddGoal()
                            showBottomSheet = true
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            averageWeeklySavings = weeklySavingAverage,
                            onAddContribution = { viewModel.openContribution(goal) },
                            onEdit = {
                                viewModel.startEditGoal(goal)
                                showBottomSheet = true
                            },
                            onDelete = { viewModel.requestDeleteGoal(goal) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Goal Bottom Sheet
    if (showBottomSheet) {
        AddEditGoalSheet(
            viewModel = viewModel,
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            }
        )
    }

    // Contribution Dialog
    contributingGoal?.let { goal ->
        GoalContributionDialog(
            goal = goal,
            amountDigits = contributionDigits,
            errorMessage = contributionError,
            onAmountDigitsChange = { viewModel.onContributionDigitsChanged(it) },
            onSubmit = { viewModel.submitContribution() },
            onDismiss = { viewModel.closeContribution() }
        )
    }

    // Delete Confirmation Dialog
    deletingGoal?.let { goal ->
        ConfirmationDialog(
            title = "Delete '${goal.name}'?",
            message = "This will remove this savings goal. Your transactions and balance will not be affected.",
            confirmText = "Delete",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = { viewModel.confirmDeleteGoal() },
            onDismiss = { viewModel.dismissDeleteGoal() }
        )
    }
}
