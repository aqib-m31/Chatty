package com.aqib.chatty.network

import com.aqib.chatty.model.RegisterLoginResponse
import com.aqib.chatty.model.UserRooms
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * ChattyApiService is an interface that defines the contract for the network API service.
 * It declares methods for registering a user, logging in a user, and getting the rooms of a user.
 */
interface ChattyApiService {
    /**
     * Register a new user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param confirmPassword The confirmed password of the user.
     * @return The response from the register operation.
     */
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): RegisterLoginResponse

    /**
     * Log in a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The response from the login operation.
     */
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): RegisterLoginResponse

    /**
     * Get the rooms of a user.
     * @param token The token of the user.
     * @param username The username of the user.
     * @return The rooms of the user.
     */
    @FormUrlEncoded
    @POST("rooms")
    suspend fun getRooms(
        @Header("Authorization") token: String,
        @Field("username") username: String
    ): UserRooms
}