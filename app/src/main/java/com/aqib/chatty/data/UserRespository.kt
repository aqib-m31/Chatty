package com.aqib.chatty.data

import com.aqib.chatty.model.RegisterLoginResponse
import com.aqib.chatty.model.UserRooms
import com.aqib.chatty.network.ChattyApiService

/**
 * UserRespository is an interface that defines the contract for user-related operations.
 * It declares methods for registering a user, logging in a user, and getting the rooms of a user.
 */
interface UserRespository {
    /**
     * Register a new user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param confirmPassword The confirmed password of the user.
     * @return The response from the register operation.
     */
    suspend fun register(
        username: String,
        password: String,
        confirmPassword: String
    ): RegisterLoginResponse

    /**
     * Log in a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The response from the login operation.
     */
    suspend fun login(username: String, password: String): RegisterLoginResponse

    /**
     * Get the rooms of a user.
     * @param token The token of the user.
     * @param username The username of the user.
     * @return The rooms of the user.
     */
    suspend fun getRooms(token: String, username: String): UserRooms
}

/**
 * NetworkUserRepository is a class that implements the UserRespository interface.
 * It uses a network API service to perform the user-related operations.
 */
class NetworkUserRepository(
    private val chattyApiService: ChattyApiService  // The network API service
) : UserRespository {
    /**
     * Register a new user by calling the register function of the API service.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param confirmPassword The confirmed password of the user.
     * @return The response from the register operation.
     */
    override suspend fun register(
        username: String,
        password: String,
        confirmPassword: String
    ): RegisterLoginResponse {
        return chattyApiService.register(
            username = username,
            password = password,
            confirmPassword = confirmPassword
        )
    }

    /**
     * Log in a user by calling the login function of the API service.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The response from the login operation.
     */
    override suspend fun login(username: String, password: String): RegisterLoginResponse {
        return chattyApiService.login(
            username = username,
            password = password
        )
    }

    /**
     * Get the rooms of a user by calling the getRooms function of the API service.
     * @param token The token of the user.
     * @param username The username of the user.
     * @return The rooms of the user.
     */
    override suspend fun getRooms(token: String, username: String): UserRooms {
        return chattyApiService.getRooms(token = "Bearer $token", username = username)
    }
}