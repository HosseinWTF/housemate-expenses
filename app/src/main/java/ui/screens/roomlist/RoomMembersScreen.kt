package com.yourpackage.ui.screens.room

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomMembersScreen(
    roomId: String,
    viewModel: RoomViewModel,
    onBack: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    val memberNames by viewModel.memberNames.collectAsState()

    var usernameToAdd by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(roomId) {
        viewModel.loadRoomMembers(roomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room Members") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = usernameToAdd,
                onValueChange = { usernameToAdd = it },
                label = { Text("Username to add") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if (usernameToAdd.isNotBlank()) {
                        findUserIdByUsername(usernameToAdd.trim()) { userId ->
                            if (userId != null) {
                                viewModel.addMemberToRoom(roomId, userId)
                                usernameToAdd = ""
                            } else {
                                errorMessage = "User not found"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Member")
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Current Members", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(members) { uid ->
                    val name = memberNames[uid] ?: uid
                    Text(text = "• $name")
                }
            }
        }
    }
}

fun findUserIdByUsername(username: String, onFound: (String?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users")
        .whereEqualTo("name", username)
        .get()
        .addOnSuccessListener { query ->
            val user = query.documents.firstOrNull()
            onFound(user?.id)
        }
        .addOnFailureListener {
            onFound(null)
        }
}
