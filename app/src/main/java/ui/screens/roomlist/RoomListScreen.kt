package com.yourpackage.ui.screens.room

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.RoomViewModel

@Composable
fun RoomListScreen(
    viewModel: RoomViewModel,
    currentUserId: String,
    onRoomClick: (roomId: String) -> Unit
) {

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
                        .padding(vertical = 4.dp)
                        .clickable { onRoomClick(room.id) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = room.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "ID: ${room.id}", style = MaterialTheme.typography.bodySmall)
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
