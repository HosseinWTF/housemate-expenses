package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.RoomViewModel

@Composable
fun JoinRoomScreen(
    viewModel: RoomViewModel,
    currentUserId: String,
    onJoined: () -> Unit,
    onBack: () -> Unit
) {
    var roomCode by remember { mutableStateOf("") }
    val joinResult by viewModel.joinResult.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Join a Room", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it },
            label = { Text("Enter Room Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack) {
                Text("Cancel")
            }

            Button(onClick = {
                viewModel.joinRoom(roomCode.trim(), currentUserId)
            }) {
                Text("Join")
            }
        }

        joinResult?.let {
            if (it.isSuccess) {
                LaunchedEffect(Unit) {
                    viewModel.clearJoinResult()
                    onJoined()
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Failed to join: ${it.exceptionOrNull()?.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
