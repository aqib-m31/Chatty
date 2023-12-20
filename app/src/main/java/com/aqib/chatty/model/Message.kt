package com.aqib.chatty.model

/**
 * MessageCategory is an enum that represents the category of a message.
 * It can be either SENT (the message was sent by the user) or RECEIVED (the message was received by the user).
 */
enum class MessageCategory {
    SENT,
    RECEIVED
}

/**
 * RoomCategory is an enum that represents the category of a room.
 * It can be either OWN (the room was created by the user) or OTHER (the room was created by another user).
 */
enum class RoomCategory {
    OWN,
    OTHER
}

/**
 * Message is a data class that represents a message.
 *
 * @property text The text of the message.
 * @property sender The sender of the message.
 * @property category The category of the message (either SENT or RECEIVED).
 */
data class Message(
    val text: String,
    val sender: String,
    val category: MessageCategory
)