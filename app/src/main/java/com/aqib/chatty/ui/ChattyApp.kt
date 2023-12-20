package com.aqib.chatty.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aqib.chatty.R
import com.aqib.chatty.ui.screens.ChatScreen
import com.aqib.chatty.ui.screens.CreateRoomScreen
import com.aqib.chatty.ui.screens.HomeScreen
import com.aqib.chatty.ui.screens.JoinRoomScreen
import com.aqib.chatty.ui.screens.LoginScreen
import com.aqib.chatty.ui.screens.RegisterScreen

/**
 * Enum class representing the different screens in the ChattyApp.
 */

enum class ChattyAppScreen {
    Register,
    Login,
    Home,
    CreateRoom,
    JoinRoom,
    Chat
}

/**
 * The main Composable function for the ChattyApp. It sets up the navigation and the top bar.
 * It also collects the state from the ViewModel and passes it to the different screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattyApp() {
    val viewModel: ChattyAppViewModel = viewModel(factory = ChattyAppViewModel.Factory)
    val userState = viewModel.userState.collectAsState()
    val chatState = viewModel.chatState.collectAsState()
    val chatUiState = viewModel.chatUiState.collectAsState()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chatty),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (userState.value.authenticated && !userState.value.joinedRoom) {
                            TextButton(onClick = {
                                viewModel.logout()
                            }) {
                                Text(text = "Logout")
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        // Navigation setup
        NavHost(
            navController = navController,
            startDestination = if (userState.value.authenticated) ChattyAppScreen.Home.name else ChattyAppScreen.Login.name
        ) {
            composable(route = ChattyAppScreen.Register.name) {
                RegisterScreen(
                    navigateLoginScreen = { navController.navigate(ChattyAppScreen.Login.name) },
                    authenticate = viewModel::authenticate,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = ChattyAppScreen.Login.name) {
                LoginScreen(
                    navigateRegisterScreen = { navController.navigate(ChattyAppScreen.Register.name) },
                    authenticate = viewModel::authenticate,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = ChattyAppScreen.Home.name) {
                val context = LocalContext.current
                HomeScreen(
                    username = userState.value.username,
                    navigateToJoinRoomScreen = {
                        navController.navigate(
                            route = ChattyAppScreen.JoinRoom.name
                        )
                    },
                    navigateToCreateRoomScreen = {
                        navController.navigate(
                            route = ChattyAppScreen.CreateRoom.name
                        )
                    },
                    contentPadding = innerPadding,
                    rooms = userState.value.joinedRooms,
                    onJoinRoom = viewModel::joinRoom,
                    onDeleteRoom = viewModel::deleteRoom,
                    isJoined = viewModel::isJoined,
                    onShare = { subject: String, roomId: String ->
                        shareRoomId(context = context, subject = subject, roomId = roomId)
                    }
                )
            }
            composable(route = ChattyAppScreen.CreateRoom.name) {
                CreateRoomScreen(
                    roomName = userState.value.newRoomName,
                    onRoomNameChange = viewModel::updateNewRoomName,
                    onCreateRoom = viewModel::createRoom,
                    message = userState.value.message,
                )
            }
            composable(route = ChattyAppScreen.JoinRoom.name) {
                JoinRoomScreen(
                    roomId = userState.value.joinedRoomId,
                    onRoomIdChange = viewModel::updateJoinedRoomId,
                    onJoinRoom = viewModel::joinRoom,
                    message = userState.value.message
                )
            }
            composable(route = ChattyAppScreen.Chat.name) {
                BackHandler(true) {
                    navController.navigate(route = ChattyAppScreen.Home.name)
                    viewModel.setJoinedToFalse()
                }
                ChatScreen(
                    roomName = userState.value.joinedRoomName,
                    messages = chatState.value.currentRoomMessages,
                    currentMessage = chatUiState.value.currentMessage,
                    onMessageChange = viewModel::updateCurrentMessage,
                    onSend = viewModel::sendMessage,
                    contentPadding = innerPadding,
                    onLeave = {
                        viewModel.leaveRoom()
                        navController.navigate(route = ChattyAppScreen.Home.name)
                    },
                    navigateToHomeScreen = {
                        navController.navigate(route = ChattyAppScreen.Home.name)
                        viewModel.setJoinedToFalse()
                    }
                )
            }
        }
        if (userState.value.joinedRoom) {
            navController.navigate(route = ChattyAppScreen.Chat.name)
        }
    }
}

/**
 * Function for sharing the room ID. It creates an intent for sharing text and starts the activity.
 *
 * @param context The context to use for starting the activity.
 * @param subject The subject of the sharing intent.
 * @param roomId The room ID to share.
 */
private fun shareRoomId(context: Context, subject: String, roomId: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, roomId)
    }
    context.startActivity(Intent.createChooser(intent, "Room Invitation"))
}