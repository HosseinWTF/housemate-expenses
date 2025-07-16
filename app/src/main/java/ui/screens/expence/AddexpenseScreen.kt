package com.yourpackage.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Expense
import viewmodel.ExpenseViewModel

@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    currentUserId: String,
    onExpenseAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val addResult by viewModel.addExpenseResult.collectAsState()

    LaunchedEffect(addResult) {
        if (addResult?.isSuccess == true) {
            viewModel.clearAddExpenseResult()
            onExpenseAdded()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add Expense", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (title.isNotBlank() && parsedAmount != null) {
                        val expense = Expense(
                            title = title.trim(),
                            amount = parsedAmount,
                            paidBy = currentUserId,
                            sharedWith = listOf(currentUserId),
                            notes = notes.trim()
                        )
                        viewModel.addExpense(expense)
                    }
                }
            ) {
                Text("Add")
            }
        }

        if (addResult?.isFailure == true) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Failed to add expense: ${addResult?.exceptionOrNull()?.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
