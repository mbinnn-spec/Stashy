package com.pocketbudget.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pocketbudget.app.PocketBudgetApplication
import com.pocketbudget.app.ui.components.ConfirmationDialog
import com.pocketbudget.app.ui.goals.GoalsScreen
import com.pocketbudget.app.ui.home.HomeScreen
import com.pocketbudget.app.ui.home.HomeViewModel
import com.pocketbudget.app.ui.settings.SettingsScreen
import com.pocketbudget.app.ui.transactions.AddEditTransactionSheet
import com.pocketbudget.app.ui.transactions.TransactionDetailDialog
import com.pocketbudget.app.ui.transactions.TransactionViewModel
import com.pocketbudget.app.ui.transactions.TransactionsScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as PocketBudgetApplication

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            transactionRepository = app.transactionRepository,
            goalRepository = app.goalRepository
        )
    )

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModel.Factory(
            transactionRepository = app.transactionRepository,
            categoryRepository = app.categoryRepository
        )
    )

    val goalViewModel: com.pocketbudget.app.ui.goals.GoalViewModel = viewModel(
        factory = com.pocketbudget.app.ui.goals.GoalViewModel.Factory(
            goalRepository = app.goalRepository,
            transactionRepository = app.transactionRepository
        )
    )

    val settingsViewModel: com.pocketbudget.app.ui.settings.SettingsViewModel = viewModel(
        factory = com.pocketbudget.app.ui.settings.SettingsViewModel.Factory(
            settingsRepository = app.settingsRepository,
            database = app.database,
            categoryRepository = app.categoryRepository
        )
    )

    var showTransactionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val detailTransaction by transactionViewModel.detailTransaction.collectAsState()
    val showDeleteConfirm by transactionViewModel.showDeleteConfirm.collectAsState()

    LaunchedEffect(Unit) {
        transactionViewModel.saveSuccessEvent.collectLatest {
            coroutineScope.launch {
                sheetState.hide()
                showTransactionSheet = false
            }
        }
    }

    Scaffold(
        bottomBar = {
            PocketBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        onNavigateToAddTransaction = {
                            transactionViewModel.startAddTransaction()
                            showTransactionSheet = true
                        },
                        onNavigateToTransactions = {
                            navController.navigate(Screen.Transactions.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToGoals = {
                            navController.navigate(Screen.Goals.route) {
                                launchSingleTop = true
                            }
                        },
                        onTransactionClick = { tx ->
                            transactionViewModel.openDetail(tx)
                        }
                    )
                }
                composable(Screen.Transactions.route) {
                    TransactionsScreen(
                        viewModel = transactionViewModel,
                        onTriggerAddTransaction = {
                            transactionViewModel.startAddTransaction()
                            showTransactionSheet = true
                        }
                    )
                }
                composable(Screen.Goals.route) {
                    GoalsScreen(viewModel = goalViewModel)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }

    // Shared Add / Edit Transaction Sheet
    if (showTransactionSheet) {
        AddEditTransactionSheet(
            viewModel = transactionViewModel,
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showTransactionSheet = false
                }
            }
        )
    }

    // Shared Transaction Detail Dialog
    detailTransaction?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { transactionViewModel.closeDetail() },
            onEdit = {
                transactionViewModel.closeDetail()
                transactionViewModel.startEditTransaction(tx)
                showTransactionSheet = true
            },
            onDeleteRequest = { transactionViewModel.requestDeleteConfirmation() }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        ConfirmationDialog(
            title = "Delete this transaction?",
            message = "This action will remove the transaction and update your balance.",
            confirmText = "Delete",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = { transactionViewModel.deleteCurrentTransaction() },
            onDismiss = { transactionViewModel.dismissDeleteConfirmation() }
        )
    }
}
