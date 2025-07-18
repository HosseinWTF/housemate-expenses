package com.yourpackage.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourpackage.ui.screens.room.RoomListScreen
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

    if (owesRecords.isNotEmpty() && userMap.isNotEmpty()) {
        Text("Who owes who:", style = MaterialTheme.typography.titleMedium)

        owesRecords.forEach { record ->
            val fromName = userMap[record.fromUid] ?: record.fromUid
            val toName = userMap[record.toUid] ?: record.toUid
            val line = "$fromName owes $toName \$${"₺%.2f".format(record.amount)}"

            Text(text = line, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }



    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
        //viewModel.loadUserNames()
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (expenses.isEmpty()) {
                Text("No expenses yet.")
            } else {
                LazyColumn {
                    items(expenses) { expense ->
                        ExpenseItem(expense)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: $${expense.amount}")
            Text(text = "Paid by: ${expense.paidBy}")
            Text(text = "Shared with: ${expense.sharedWith.size} people")
            if (expense.notes.isNotBlank()) {
                Text(text = "Notes: ${expense.notes}")
            }
        }
    }
}
