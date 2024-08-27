package com.aqib.chatty.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aqib.chatty.BuildConfig
import com.aqib.chatty.ChattyApplication
import com.aqib.chatty.data.AccessTokenPreferencesRepository
import com.aqib.chatty.data.UserRespository
import com.aqib.chatty.model.Message
import com.aqib.chatty.model.MessageCategory
import com.aqib.chatty.model.UserRooms
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import org.json.JSONException
import org.json.JSONObject

/**
 * UserState is a data class that represents the state of the user in the application.
 *
 * @property username The username of the user.
 * @property accessToken The access token of the user.
 * @property authenticated A boolean indicating whether the user is authenticated.
 * @property newRoomName The name of the new room that the user wants to create.
 * @property joinedRoomId The id of the room that the user has joined.
 * @property joinedRoomName The name of the room that the user has joined.
 * @property joinedRoom A boolean indicating whether the user has joined a room.
 * @property joinedRooms The rooms that the user has joined.
 * @property errors A list of error messages.
 * @property message A message to be displayed to the user.
 */
data class UserState(
    val username: String = "",
    val accessToken: String = "",
    val authenticated: Boolean = false,
    val newRoomName: String = "",
    val joinedRoomId: String = "",
    val joinedRoomName: String = "",
    val joinedRoom: Boolean = false,
    val joinedRooms: UserRooms = UserRooms(own = emptyList(), others = emptyList()),
    val errors: List<String> = emptyList(),
    val message: String = ""
)

/**
 * ChatState is a data class that represents the state of the chat in the current room.
 *
 * @property currentRoomMessages The messages in the current room.
 */
data class ChatState(
    val currentRoomMessages: List<Message> = emptyList()
)

/**
 * ChatUiState is a data class that represents the UI state of the chat screen.
 *
 * @property currentMessage The message that the user is currently typing.
 */
data class ChatUiState(
    val currentMessage: String = ""
)


/**
 * ChattyAppViewModel is a class that manages the state and business logic for the application.
 * It communicates with the UserRepository and AccessTokenPreferencesRepository to perform user-related operations and manage access tokens.
 * It also handles the socket connection and emits and listens for socket events.
 * It exposes state flows of UserState, ChatState, and ChatUiState for observing the state from the UI layer.
 *
 * @property userRepository The repository for performing user-related operations.
 * @property accessTokenPreferencesRepository The repository for managing access tokens and usernames in a data store.
 */
class ChattyAppViewModel(
    private val userRepository: UserRespository,
    private val accessTokenPreferencesRepository: AccessTokenPreferencesRepository
) : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    // Socket Connection
    private var socket: Socket? = null

    /**
     * Initializes the ViewModel. It collects the access token and username from the repository and updates the user state.
     * It also connects to the socket if the access token is not empty.
     */
    init {
        viewModelScope.launch {
            accessTokenPreferencesRepository.accessToken.collect { accessToken ->
                _userState.update { currentState ->
                    currentState.copy(
                        accessToken = accessToken,
                        authenticated = accessToken.isNotEmpty()
                    )
                }
                if (accessToken.isNotEmpty()) {
                    connect()
                }
            }
        }

        viewModelScope.launch {
            accessTokenPreferencesRepository.username.collect { username ->
                _userState.update { currentState ->
                    currentState.copy(
                        username = username,
                    )
                }

                if (_userState.value.authenticated) {
                    try {
                        val rooms = userRepository.getRooms(_userState.value.accessToken, username)
                        _userState.update { currentState ->
                            currentState.copy(
                                joinedRooms = rooms
                            )
                        }
                    } catch (e: retrofit2.HttpException) {
                        Log.d("INIT RETRIEVE ROOMS", "${e.message}")
                        if (e.code() in 400..499) {
                            _userState.update { currentState ->
                                currentState.copy(
                                    message = "FORBIDDEN",
                                    authenticated = false
                                )
                            }
                        } else {
                            _userState.update { currentState ->
                                currentState.copy(
                                    message = "SERVER ERROR"
                                )
                            }
                        }
                    } catch (e: IOException) {
                        Log.d("INIT RETRIEVE ROOMS", e.message.toString())
                    } catch (e: Exception) {
                        Log.d("INIT RETRIEVE ROOMS", e.message.toString())
                    }
                }
            }
        }
    }

    /**
     * Sets up the socket listeners for the "message", "delete_response", and "join_response" events.
     */
    private fun setup() {
        try {
            Log.d("SOCKET SETUP", "Connected Successfully.")

            socket?.on("message") { messageResponse ->
                val data = messageResponse[0] as JSONObject
                val sender = data.getString("sender")
                val message = Message(
                    text = data.getString("message"),
                    sender = sender,
                    category = if (sender == userState.value.username) MessageCategory.SENT else MessageCategory.RECEIVED
                )
                _chatState.update { currentState ->
                    currentState.copy(
                        currentRoomMessages = currentState.currentRoomMessages + message
                    )
                }
                Log.d("SOCKET MESSAGE", data.toString())
            }

            socket?.on("delete_response") { args ->
                val data = args[0] as JSONObject
                val error = data.getBoolean("error")
                val message = data.getString("message")
                if (!error) {
                    viewModelScope.launch {
                        _userState.update { currentState ->
                            currentState.copy(
                                joinedRooms = userRepository.getRooms(
                                    _userState.value.accessToken,
                                    _userState.value.username
                                ),
                                joinedRoomName = "",
                                joinedRoomId = "",
                                joinedRoom = false,
                                message = message
                            )
                        }
                        _chatState.update { currentState ->
                            currentState.copy(
                                currentRoomMessages = emptyList()
                            )
                        }
                    }
                }
                Log.d("SOCKET DELETE", data.toString())
            }

            socket?.on("join_response") { args ->
                Log.d("SOCKET JOIN", args[0].toString())
                val data = args[0] as JSONObject
                try {
                    val message = data.getString("message")
                    val roomId = data.getString("roomId")
                    val roomName = data.getString("roomName")

                    viewModelScope.launch {
                        _chatState.update { currentState ->
                            val clearMessages: Boolean = roomName != _userState.value.joinedRoomName
                            currentState.copy(
                                currentRoomMessages = if (clearMessages) emptyList() else currentState.currentRoomMessages
                            )
                        }
                        _userState.update { currentState ->
                            currentState.copy(
                                message = message,
                                joinedRoomId = roomId,
                                joinedRoom = true,
                                joinedRoomName = roomName,
                                joinedRooms = userRepository.getRooms(
                                    _userState.value.accessToken,
                                    _userState.value.username
                                ),
                            )
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("SOCKET JOIN", e.message.toString())
                } catch (e: Exception) {
                    Log.d("SOCKET JOIN", e.message.toString())
                }
            }
        } catch (e: Exception) {
            Log.d("SOCKET SETUP", e.message.toString())
        }
    }

    /**
     * Connects to the socket and sets up the socket listeners.
     */
    private fun connect() {
        viewModelScope.launch {
            accessTokenPreferencesRepository.accessToken.collect { accessToken ->
                if (accessToken.isNotEmpty()) {
                    socket = IO.socket(BuildConfig.SERVER_URL, IO.Options().apply {
                        extraHeaders = mapOf(
                            "Authorization" to listOf("Bearer $accessToken")
                        )
                    })
                    if (socket?.connected() == false) {
                        socket?.connect()
                        setup()
                        _userState.update { currentState ->
                            currentState.copy(message = "Connecting... May be the server is booting up!")
                        }
                        socket?.on(Socket.EVENT_CONNECT) {
                            _userState.update { currentState ->
                                currentState.copy(message = "Connected Successfully!")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Logs out the user. It disconnects from the socket and clears the user and chat states.
     */
    fun logout() {
        viewModelScope.launch {
            if (socket?.connected() == true) {
                try {
                    if (_userState.value.joinedRoomId.isNotEmpty()) {
                        socket?.emit(
                            "temp_leave",
                            JSONObject().put("roomId", _userState.value.joinedRoomId)
                        )
                    }
                    _userState.update { currentState ->
                        currentState.copy(
                            joinedRoom = false,
                            joinedRoomId = "",
                            joinedRoomName = "",
                            authenticated = false
                        )
                    }
                    _chatState.update { currentState ->
                        currentState.copy(currentRoomMessages = emptyList())
                    }
                    accessTokenPreferencesRepository.setAccessToken("")
                    socket?.disconnect()
                } catch (e: Exception) {
                    Log.d("SOCKET LOGOUT", e.message.toString())
                }
            }
        }
    }

    /**
     * Creates a new room. It emits a "create" event to the socket.
     */
    fun createRoom() {
        val roomName = _userState.value.newRoomName

        try {
            if (socket?.connected() == true) {
                if (_userState.value.joinedRoomId.isNotEmpty()) {
                    socket?.emit(
                        "temp_leave",
                        JSONObject().put("roomId", _userState.value.joinedRoomId)
                    )
                }
                socket?.emit("create", JSONObject().put("room", roomName))
                socket?.on("create_response") { args ->
                    Log.d("SOCKET CREATE", args[0].toString())
                    val data = args[0] as JSONObject
                    val message = data.getString("message")
                    _userState.update { currentState ->
                        currentState.copy(
                            message = message
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("SOCKET CREATE", e.message.toString())
        }
    }

    /**
     * Leaves a room. It emits a "leave" event to the socket.
     */
    fun leaveRoom() {
        val roomId = _userState.value.joinedRoomId
        val leaveRoomObject = JSONObject()
            .put("roomId", roomId)

        try {
            if (socket?.connected() == true) {
                socket?.emit("leave", leaveRoomObject)
                socket?.on("leave_response") { args ->
                    Log.d("SOCKET LEAVE", args[0].toString())
                    val data = args[0] as JSONObject
                    try {
                        val message = data.getString("message")
                        viewModelScope.launch {
                            _userState.update { currentState ->
                                currentState.copy(
                                    message = message,
                                    joinedRoom = false,
                                    joinedRoomId = "",
                                    joinedRoomName = "",
                                    joinedRooms = userRepository.getRooms(
                                        _userState.value.accessToken,
                                        _userState.value.username
                                    )
                                )
                            }
                            _chatState.update { currentState ->
                                currentState.copy(currentRoomMessages = emptyList())
                            }
                        }
                    } catch (e: JSONException) {
                        Log.d("SOCKET LEAVE", e.message.toString())
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("SOCKET LEAVE", e.message.toString())
        }
    }

    /**
     * Joins a room. It emits a "join" or "switch" event to the socket.
     */
    fun joinRoom(roomId: String = _userState.value.joinedRoomId) {
        val joinedRoomId = _userState.value.joinedRoomId
        val joinObject = JSONObject()
            .put("roomId", roomId)

        if (socket?.connected() == true) {
            try {
                if (joinedRoomId.isNotEmpty() && roomId != joinedRoomId) {
                    val switchRoomObject = JSONObject()
                        .put("joinRoom", roomId)
                        .put("leaveRoom", _userState.value.joinedRoomId)
                    socket?.emit("switch", switchRoomObject)
                    _chatState.update { currentState ->
                        currentState.copy(currentRoomMessages = emptyList())
                    }
                } else {
                    socket?.emit("join", joinObject)
                }
            } catch (e: Exception) {
                Log.d("SOCKET JOIN", e.message.toString())
            }
        }
    }

    /**
     * Sets the joinedRoom property of the user state to false.
     */
    fun setJoinedToFalse() {
        _userState.update { currentState ->
            currentState.copy(
                joinedRoom = false
            )
        }
    }

    /**
     * Checks if the user has joined a room.
     */
    fun isJoined(roomId: String): Boolean {
        return roomId == _userState.value.joinedRoomId
    }

    /**
     * Deletes a room. It emits a "delete" event to the socket.
     */
    fun deleteRoom(roomId: String) {
        if (socket?.connected() == true) {
            try {
                val deleteRoomObject = JSONObject()
                    .put("roomId", roomId)
                socket?.emit("delete", deleteRoomObject)
            } catch (e: Exception) {
                Log.d("SOCKET DELETE", e.message.toString())
            }
        }
    }

    /**
     * Authenticates the user. It sets the authenticated property of the user state to true.
     */
    fun authenticate() {
        _userState.update { currentState ->
            currentState.copy(
                authenticated = true
            )
        }
    }

    /**
     * Updates the joinedRoomId property of the user state.
     */
    fun updateJoinedRoomId(roomId: String) {
        _userState.update { currentState ->
            currentState.copy(
                joinedRoomId = roomId
            )
        }
    }

    /**
     * Updates the newRoomName property of the user state.
     */
    fun updateNewRoomName(roomName: String) {
        _userState.update { currentState ->
            currentState.copy(
                newRoomName = roomName
            )
        }
    }

    /**
     * Updates the currentMessage property of the chat UI state.
     */
    fun updateCurrentMessage(message: String) {
        _chatUiState.update { currentState ->
            currentState.copy(
                currentMessage = message
            )
        }
    }

    /**
     * Sends a message. It emits a "message" event to the socket.
     */
    fun sendMessage() {
        if (chatUiState.value.currentMessage.isEmpty()) {
            return;
        }

        if (socket?.connected() == true) {
            val message = Message(
                text = chatUiState.value.currentMessage,
                sender = _userState.value.username,
                category = MessageCategory.SENT
            )
            val messageObject = JSONObject()
                .put("message", message.text)
                .put("room", _userState.value.joinedRoomName)
                .put("sender", message.sender)

            try {
                socket?.emit("message", messageObject)
                _chatUiState.update { currentState ->
                    currentState.copy(
                        currentMessage = ""
                    )
                }
            } catch (e: Exception) {
                Log.d("SOCKET MESSAGE", e.message.toString())
            }
        }
    }

    /**
     * A factory for creating instances of the ChattyAppViewModel class.
     * It uses the application container to get instances of UserRespository and AccessTokenPreferencesRepository.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChattyApplication)
                ChattyAppViewModel(
                    application.container.userRespository,
                    application.container.accessTokenPreferencesRepository
                )
            }
        }
    }
}