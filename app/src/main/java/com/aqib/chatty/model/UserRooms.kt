package com.aqib.chatty.model

import kotlinx.serialization.Serializable

/**
 * Room is a data class that represents a chat room.
 *
 * @property id The unique identifier of the room.
 * @property name The name of the room.
 */
@Serializable
data class Room(val id: String, val name: String)

/**
 * UserRooms is a data class that represents the rooms of a user.
 *
 * @property own A list of rooms that the user owns.
 * @property others A list of rooms that the user is a member of but does not own.
 */
@Serializable
data class UserRooms(
    val own: List<Room>,
    val others: List<Room>
)