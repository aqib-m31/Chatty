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
 * Composable function for the Create Room Screen.
 * It displays a form for the user to enter the room name and a button to create the room.
 *
 * @param roomName The room name entered by the user.
 * @param onRoomNameChange A function to be called when the user changes the room name.
 * @param onCreateRoom A function to be called when the user clicks on the "Create Room" button.
 * @param message A message to be displayed to the user.
 * @param modifier A Modifier to be applied to the Column composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    roomName: String,
    onRoomNameChange: (String) -> Unit,
    onCreateRoom: () -> Unit,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = roomName, onValueChange = onRoomNameChange,
            label = {
                Text(text = "Enter Room Name")
            },
            supportingText = {
                Text(text = message)
            }
        )
        Button(
            onClick = onCreateRoom,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(text = "Create Room")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRoomScreenPreview() {
    ChattyTheme {
        CreateRoomScreen("", {}, {}, "")
    }
}