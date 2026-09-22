package com.pocketbudget.app.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketbudget.app.domain.model.TransactionType
import com.pocketbudget.app.ui.components.CategoryIconHelper
import com.pocketbudget.app.ui.components.EmptyStateView
import com.pocketbudget.app.ui.components.TransactionItem
import com.pocketbudget.app.ui.theme.ExpenseRed
import com.pocketbudget.app.ui.theme.IncomeGreen

@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel,
    onTriggerAddTransaction: () -> Unit = {}
) {
    val groupedTransactions by viewModel.groupedTransactions.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val filterCategoryId by viewModel.filterCategoryId.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val availableCategories by viewModel.availableFilterCategories.collectAsState()

    val isAnyFilterActive = filterType != null || filterCategoryId != null || dateFilter != DateFilterOption.ALL || searchQuery.isNotEmpty()

    val focusManager = LocalFocusManager.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTriggerAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Transaction",
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
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Page Title Row with Clear All Filter button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (isAnyFilterActive) {
                    TextButton(onClick = { viewModel.clearAllFilters() }) {
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search note or category...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Type & Date Options Row (Horizontally scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type Filters
                FilterChip(
                    selected = filterType == null,
                    onClick = { viewModel.setFilterType(null) },
                    label = { Text("All") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                FilterChip(
                    selected = filterType == TransactionType.INCOME,
                    onClick = { viewModel.setFilterType(TransactionType.INCOME) },
                    label = { Text("Income") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen.copy(alpha = 0.18f),
                        selectedLabelColor = IncomeGreen
                    )
                )

                FilterChip(
                    selected = filterType == TransactionType.EXPENSE,
                    onClick = { viewModel.setFilterType(TransactionType.EXPENSE) },
                    label = { Text("Expense") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed.copy(alpha = 0.18f),
                        selectedLabelColor = ExpenseRed
                    )
                )

                // Date Filters
                DateFilterOption.entries.filter { it != DateFilterOption.ALL }.forEach { option ->
                    val isDateSelected = dateFilter == option
                    FilterChip(
                        selected = isDateSelected,
                        onClick = {
                            if (isDateSelected) {
                                viewModel.setDateFilter(DateFilterOption.ALL)
                            } else {
                                viewModel.setDateFilter(option)
                            }
                        },
                        label = { Text(option.label) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Row
            if (availableCategories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterCategoryId == null,
                            onClick = { viewModel.setFilterCategory(null) },
                            label = { Text("All Categories") },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    items(availableCategories, key = { it.id }) { cat ->
                        val isSelected = filterCategoryId == cat.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.setFilterCategory(null)
                                } else {
                                    viewModel.setFilterCategory(cat.id)
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = CategoryIconHelper.getIcon(cat.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else CategoryIconHelper.parseColor(cat.colorHex)
                                )
                            },
                            label = { Text(cat.name) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transaction History List or Empty State
            if (groupedTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = if (isAnyFilterActive) "No matching transactions" else "No transactions yet.",
                        description = if (isAnyFilterActive)
                            "Try adjusting or clearing your filters"
                        else
                            "Start tracking your money by adding your first transaction.",
                        actionButtonText = if (!isAnyFilterActive) "+ Add Transaction" else "Clear Filters",
                        onActionClick = {
                            if (!isAnyFilterActive) {
                                onTriggerAddTransaction()
                            } else {
                                viewModel.clearAllFilters()
                            }
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedTransactions.forEach { (dateHeader, transactionsForDay) ->
                        item(key = "header_$dateHeader") {
                            Text(
                                text = dateHeader,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                            )
                        }

                        items(transactionsForDay, key = { it.id }) { tx ->
                            TransactionItem(
                                transaction = tx,
                                onClick = { viewModel.openDetail(tx) }
                            )
                        }
                    }
                }
            }
        }
    }
}
