package com.aqib.chatty.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * RegisterLoginResponse is a data class that represents the response from the register or login operation.
 *
 * @property error A Boolean indicating whether an error occurred during the operation.
 * @property message A String containing a message about the operation.
 * @property username A nullable String for the username of the user. It's null if the operation was unsuccessful.
 * @property accessToken A nullable String for the access token of the user. It's null if the operation was unsuccessful.
 */
@Serializable
data class RegisterLoginResponse(
    val error: Boolean,
    val message: String,
    val username: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
)