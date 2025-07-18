package com.yourpackage.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Expense
import viewmodel.ExpenseViewModel

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    onAddExpenseClick: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMap by viewModel.userMap.collectAsState()
    val owesRecords = viewModel.calculateOwes()

    LaunchedEffect(expenses) {
        val userIds = expenses.flatMap { it.sharedWith + it.paidBy }.distinct()
        viewModel.loadUserNames(userIds)
    }

    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Expenses", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Balances", style = MaterialTheme.typography.titleMedium)
            if (owesRecords.isNotEmpty()) {
                owesRecords.forEach { record ->
                    val fromName = userMap[record.fromUid] ?: record.fromUid
                    val toName = userMap[record.toUid] ?: record.toUid
                    val amount = String.format("₺%.2f", record.amount)
                    Text("• $fromName owes $toName $amount")
                }
            } else {
                Text("No balances yet.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (expenses.isEmpty()) {
                Text("No expenses yet.")
            } else {
                LazyColumn {
                    items(expenses) { expense ->
                        ExpenseItem(expense = expense, userMap = userMap)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, userMap: Map<String, String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = expense.title, style = MaterialTheme.typography.titleMedium)

            Text(text = "Amount: ₺${"%.2f".format(expense.amount)}")

            val paidByName = userMap[expense.paidBy] ?: expense.paidBy
            Text(text = "Paid by: $paidByName")

            val sharedNames = expense.sharedWith.map { userMap[it] ?: it }
            Text(text = "Shared with: ${sharedNames.joinToString(", ")}")

            if (expense.notes.isNotBlank()) {
                Text(text = "Notes: ${expense.notes}")
            }
        }
    }
}



