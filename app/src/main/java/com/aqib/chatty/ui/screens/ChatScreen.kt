package com.aqib.chatty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aqib.chatty.R
import com.aqib.chatty.model.Message
import com.aqib.chatty.model.MessageCategory
import com.aqib.chatty.ui.theme.ChattyTheme

/**
 * Composable function for the Chat Screen.
 * It displays the room name, a list of messages, a text field for the user to enter a message, and buttons to send a message, leave the room, and go back to the home screen.
 *
 * @param roomName The name of the room.
 * @param messages The messages in the room.
 * @param contentPadding The padding to be applied to the content of the screen.
 * @param currentMessage The message currently being typed by the user.
 * @param onMessageChange A function to be called when the user changes the message.
 * @param onSend A function to be called when the user clicks on the "Send" button.
 * @param onLeave A function to be called when the user clicks on the "Leave Room" button.
 * @param navigateToHomeScreen A function to be called when the user clicks on the "Go Back to Home Screen" button.
 * @param modifier A Modifier to be applied to the Column composable.
 */
@Composable
fun ChatScreen(
    roomName: String,
    messages: List<Message>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onLeave: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (roomName.isEmpty()) navigateToHomeScreen()
    Column(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                text = "Room: $roomName",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            )
            Spacer(modifier = modifier.weight(1f))
            IconButton(onClick = navigateToHomeScreen) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Go Back to Home Screen",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onLeave) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Leave Room",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        val lazyListState = rememberLazyListState()
        LaunchedEffect(messages) {
            lazyListState.scrollToItem(if (messages.isNotEmpty()) messages.size - 1 else 0)
        }
        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .padding(top = dimensionResource(id = R.dimen.padding_medium))
                .weight(1f)
                .fillMaxSize(),
        ) {
            items(messages) { message ->
                Row(
                    modifier = modifier.fillMaxWidth()
                ) {
                    MessageCard(message = message, modifier)
                }
            }
        }
        MessageBox(message = currentMessage, onMessageChange = onMessageChange, onSend = onSend)
    }
}

/**
 * Composable function for a Message Card.
 * It displays the sender's username, the message text, and a background color depending on whether the message was sent or received.
 *
 * @param message The message to be displayed.
 * @param modifier A Modifier to be applied to the Row composable.
 */
@Composable
fun MessageCard(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (message.category == MessageCategory.SENT) dimensionResource(id = R.dimen.message_padding) else dimensionResource(
                    id = R.dimen.padding_medium
                ),
                top = dimensionResource(id = R.dimen.padding_extra_small),
                bottom = dimensionResource(id = R.dimen.padding_extra_small),
                end = if (message.category == MessageCategory.RECEIVED) dimensionResource(id = R.dimen.message_padding) else dimensionResource(
                    id = R.dimen.padding_medium
                ),
            ),
        horizontalArrangement = if (message.category == MessageCategory.RECEIVED) Arrangement.Start else Arrangement.End,
    ) {
        Column(
            modifier = modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.category == MessageCategory.RECEIVED) 1.dp else 16.dp,
                        bottomEnd = if (message.category == MessageCategory.SENT) 1.dp else 16.dp
                    )
                )
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier.padding(
                    dimensionResource(id = R.dimen.padding_medium)
                )
            ) {
                Text(
                    text = "@${message.sender}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = message.text,
                    modifier = modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}

/**
 * Composable function for a Message Box.
 * It displays a text field for the user to enter a message and a button to send the message.
 *
 * @param message The message currently being typed by the user.
 * @param onMessageChange A function to be called when the user changes the message.
 * @param onSend A function to be called when the user clicks on the "Send" button.
 * @param modifier A Modifier to be applied to the Row composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageBox(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = {
                Text(text = "Message")
            },
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            maxLines = 4
        )
        FilledIconButton(onClick = onSend) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send Message",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChattyTheme {
        ChatScreen(
            roomName = "Tripod",
            messages = listOf(
                Message("Heya I'm Aqib", "aqib", MessageCategory.RECEIVED),
                Message("hello I'm Mutaib", "mutaib", MessageCategory.SENT),
                Message("Hey I'm Danish", "danish", MessageCategory.RECEIVED),
            ),
            currentMessage = "Hey, Test Message",
            onMessageChange = {},
            onSend = {},
            onLeave = {},
            navigateToHomeScreen = {}
        )
    }
}
