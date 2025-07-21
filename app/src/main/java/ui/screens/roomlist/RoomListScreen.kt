package com.yourpackage.ui.screens.room

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.RoomViewModel

@Composable
fun RoomListScreen(
    viewModel: RoomViewModel,
    currentUserId: String,
    onRoomClick: (roomId: String) -> Unit,
    onManageMembersClick: (roomId: String) -> Unit
)
 {
    val rooms by viewModel.rooms.collectAsState()
    val createResult by viewModel.createResult.collectAsState()

    var roomName by remember { mutableStateOf("") }

    LaunchedEffect(currentUserId) {
        viewModel.startListeningToUserRooms(currentUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Rooms", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("New Room Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (roomName.isNotBlank()) {
                    viewModel.createRoom(roomName.trim(), currentUserId)
                    roomName = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Room")
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(rooms) { room ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable { onRoomClick(room.id) }
                        )
                        Text(text = "ID: ${room.id}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onManageMembersClick(room.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Members")
                        }
                    }
                    Button(onClick = { onManageMembersClick(room.id) }) {
                        Text("Manage Members")
                    }
                }
            }
        }

        if (createResult?.isFailure == true) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Failed to create room: ${createResult?.exceptionOrNull()?.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
