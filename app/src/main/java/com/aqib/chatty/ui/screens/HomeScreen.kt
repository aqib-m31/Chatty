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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aqib.chatty.R
import com.aqib.chatty.model.RoomCategory
import com.aqib.chatty.model.UserRooms
import com.aqib.chatty.ui.theme.ChattyTheme

/**
 * Composable function for the Home Screen.
 * It displays the user's username, a list of rooms, and buttons to join a room, create a room, and logout.
 *
 * @param username The username of the user.
 * @param navigateToJoinRoomScreen A function to be called when the user clicks on the "Join Room" button.
 * @param navigateToCreateRoomScreen A function to be called when the user clicks on the "Create Room" button.
 * @param rooms The rooms that the user has joined.
 * @param onJoinRoom A function to be called when the user clicks on the "Join" button of a room.
 * @param onDeleteRoom A function to be called when the user clicks on the "Delete" button of a room.
 * @param isJoined A function to check if the user has joined a room.
 * @param onShare A function to be called when the user clicks on the "Share" button of a room.
 * @param modifier A Modifier to be applied to the Scaffold composable.
 */
@Composable
fun HomeScreen(
    username: String,
    navigateToJoinRoomScreen: () -> Unit,
    navigateToCreateRoomScreen: () -> Unit,
    rooms: UserRooms,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onJoinRoom: (String) -> Unit,
    onDeleteRoom: (String) -> Unit,
    isJoined: (String) -> Boolean,
    onShare: (String, String) -> Unit,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                text = "Joined Rooms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(
                    vertical = dimensionResource(id = R.dimen.padding_small),
                    horizontal = dimensionResource(
                        id = R.dimen.padding_large
                    )
                )
            )
            Spacer(modifier = modifier.weight(1f))
            Text(
                text = "Signed in as ",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small))
            )
            Text(
                text = username,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(end = dimensionResource(id = R.dimen.padding_large))
            )
        }
        if (rooms.own.isEmpty() && rooms.others.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .weight(1f)
                    .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            ) {
                Text(text = message)
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            ) {
                items(rooms.own) { room ->
                    RoomRow(
                        room = Pair(room.id, room.name),
                        roomCategory = RoomCategory.OWN,
                        onDeleteRoom = onDeleteRoom,
                        onJoinRoom = onJoinRoom,
                        isJoined = isJoined,
                        onShare = onShare
                    )
                }
                items(rooms.others) { room ->
                    RoomRow(
                        room = Pair(room.id, room.name),
                        roomCategory = RoomCategory.OTHER,
                        onDeleteRoom = {},
                        onJoinRoom = onJoinRoom,
                        isJoined = isJoined,
                        onShare = onShare
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Button(
                onClick = navigateToJoinRoomScreen,
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Join Room")
            }
            Spacer(modifier = modifier.width(dimensionResource(id = R.dimen.small_spacer)))
            Button(
                onClick = navigateToCreateRoomScreen,
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Create a new room")
            }
        }
    }
}

/**
 * Composable function for a Room Row.
 * It displays the room name, a "Joined" badge if the user has joined the room, and buttons to delete the room, share the room ID, and join the room.
 *
 * @param room A pair of room ID and room name.
 * @param roomCategory The category of the room (own or others).
 * @param onDeleteRoom A function to be called when the user clicks on the "Delete Room" button.
 * @param onJoinRoom A function to be called when the user clicks on the "Join" button.
 * @param isJoined A function to check if the user has joined the room.
 * @param onShare A function to be called when the user clicks on the "Share Room ID" button.
 * @param modifier A Modifier to be applied to the Surface composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomRow(
    room: Pair<String, String>,
    roomCategory: RoomCategory,
    onDeleteRoom: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    isJoined: (String) -> Boolean,
    onShare: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(
            topEnd = 16.dp,
            bottomStart = 16.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_small)
            )
    ) {
        Row(
            modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = room.second,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (isJoined(room.first)) {
                Badge(
                    modifier = modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(text = "Joined")
                }
            }
            Spacer(modifier = modifier.weight(1f))
            if (roomCategory == RoomCategory.OWN) {
                IconButton(onClick = { onDeleteRoom(room.first) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Room",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { onShare("Room Invitation", room.first) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share Room ID",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            ElevatedButton(
                onClick = { onJoinRoom(room.first) }) {
                Text(text = "Join")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ChattyTheme {
        HomeScreen(
            username = "aqib_m31",
            navigateToJoinRoomScreen = { /*TODO*/ },
            navigateToCreateRoomScreen = { /*TODO*/ },
            rooms = UserRooms(own = emptyList(), others = emptyList()),
            onJoinRoom = {},
            onDeleteRoom = {},
            isJoined = { _ -> false },
            onShare = { _, _ -> {} },
            message = ""
        )
    }
}