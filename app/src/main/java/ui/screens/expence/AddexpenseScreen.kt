package com.yourpackage.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import data.model.Expense
import kotlinx.coroutines.tasks.await
import viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    currentUserId: String,
    roomId: String,
    onExpenseAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val addResult by viewModel.addExpenseResult.collectAsState()
    val userMap by viewModel.userMap.collectAsState()

    var members by remember { mutableStateOf<List<String>>(emptyList()) }
    var sharedWith by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val roomSnapshot = FirebaseFirestore.getInstance()
            .collection("rooms")
            .document(roomId)
            .get()
            .await()

        val roomMembers = roomSnapshot.get("members") as? List<String> ?: emptyList()
        members = roomMembers
        sharedWith = roomMembers // default: share with all
        viewModel.loadUserNames(roomMembers)
    }

    // Handle success
    LaunchedEffect(addResult) {
        if (addResult?.isSuccess == true) {
            viewModel.clearAddExpenseResult()
            onExpenseAdded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Expense") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
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
                label = { Text("Amount (₺)") },
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

            UserSelector(
                members = members,
                userMap = userMap,
                selected = sharedWith,
                onChange = { sharedWith = it }
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
                        if (title.isNotBlank() && parsedAmount != null && sharedWith.isNotEmpty()) {
                            val expense = Expense(
                                title = title.trim(),
                                amount = parsedAmount,
                                paidBy = currentUserId,
                                sharedWith = sharedWith,
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
}

@Composable
fun UserSelector(
    members: List<String>,
    userMap: Map<String, String>,
    selected: List<String>,
    onChange: (List<String>) -> Unit
) {
    var selectedUsers by remember { mutableStateOf(selected.toSet()) }

    Column {
        Text("Split with:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            selectedUsers = members.toSet()
            onChange(selectedUsers.toList())
        }) {
            Text("Share with All")
        }

        Spacer(modifier = Modifier.height(8.dp))

        members.forEach { uid ->
            val name = userMap[uid] ?: uid
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uid in selectedUsers,
                    onCheckedChange = { checked ->
                        selectedUsers = if (checked)
                            selectedUsers + uid
                        else
                            selectedUsers - uid
                        onChange(selectedUsers.toList())
                    }
                )
                Text(name)
            }
        }
    }
}
