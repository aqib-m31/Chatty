package com.aqib.chatty.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.aqib.chatty.R
import com.aqib.chatty.ui.theme.ChattyTheme

/**
 * Composable function for the Join Room Screen.
 * It displays a form for the user to enter the room ID and a button to join the room.
 *
 * @param roomId The room ID entered by the user.
 * @param onRoomIdChange A function to be called when the user changes the room ID.
 * @param onJoinRoom A function to be called when the user clicks on the "Join Room" button.
 * @param message A message to be displayed to the user.
 * @param modifier A Modifier to be applied to the Column composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomScreen(
    roomId: String,
    onRoomIdChange: (String) -> Unit,
    onJoinRoom: () -> Unit,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = roomId, onValueChange = onRoomIdChange,
            label = {
                Text(text = "Enter Room ID")
            },
            supportingText = {
                Text(text = message)
            }
        )
        Button(
            onClick = onJoinRoom,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(text = "Join Room")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinRoomScreenPreview() {
    ChattyTheme {
        JoinRoomScreen("", {}, {}, "")
    }
}