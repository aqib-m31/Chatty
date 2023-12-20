package com.aqib.chatty.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aqib.chatty.ChattyApplication
import com.aqib.chatty.data.AccessTokenPreferencesRepository
import com.aqib.chatty.data.UserRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * RegisterUiState is a data class that represents the UI state of the register screen.
 *
 * @property username The username entered by the user. Default value is "Hello".
 * @property password The password entered by the user. Default value is "asdfghjkl".
 * @property confirmPassword The confirmed password entered by the user. Default value is "asdfghjkl".
 * @property errors A list of error messages. Default value is an empty list.
 */
data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errors: List<String> = emptyList()
)


/**
 * RegisterViewModel is a class that manages the UI state and business logic for the register screen.
 * It communicates with the UserRepository and AccessTokenPreferencesRepository to perform user registration and store access tokens.
 * It exposes a state flow of RegisterUiState for observing the UI state from the UI layer.
 * It provides methods for updating the username, password, and confirmed password, and for performing the registration operation.
 *
 * @property userRepository The repository for performing user-related operations.
 * @property accessTokenPreferencesRepository The repository for managing access tokens and usernames in a data store.
 */
class RegisterViewModel(
    private val userRepository: UserRespository,
    private val accessTokenPreferencesRepository: AccessTokenPreferencesRepository
) : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState.asStateFlow()

    /**
     * Updates the username in the UI state.
     *
     * @param username The new username.
     */
    fun updateUsername(username: String) {
        _registerUiState.update { currentState ->
            currentState.copy(
                username = username
            )
        }
    }

    /**
     * Updates the password in the UI state.
     *
     * @param password The new password.
     */
    fun updatePassword(password: String) {
        _registerUiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    /**
     * Updates the confirmed password in the UI state.
     *
     * @param confirmPassword The new confirmed password.
     */
    fun updateConfirmPassword(confirmPassword: String) {
        _registerUiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword
            )
        }
    }

    /**
     * Performs the registration operation.
     * It first validates the input. If the input is valid, it attempts to register the user.
     * If the registration is successful, it stores the access token and username in the data store.
     * If the registration is unsuccessful, it updates the UI state with the error message.
     * It catches any exceptions that might occur during the operation and logs them.
     *
     * @param authenticate A function to be called if the registration is successful.
     */
    fun register(authenticate: () -> Unit) {
        validate()
        viewModelScope.launch {
            try {
                val info = userRepository.register(
                    _registerUiState.value.username,
                    _registerUiState.value.password,
                    _registerUiState.value.confirmPassword
                )
                if (info.accessToken != null && info.username != null) {
                    accessTokenPreferencesRepository.setAccessToken(info.accessToken)
                    accessTokenPreferencesRepository.setUsername(info.username)
                    accessTokenPreferencesRepository.username.collect { data ->
                        Log.d("TEST", data)
                    }
                }
                if (!info.error) {
                    authenticate()
                } else {
                    _registerUiState.update { currentState ->
                        currentState.copy(errors = listOf(info.message))
                    }
                }
                Log.d("REGISTER", info.toString())

            } catch (e: HttpException) {
                Log.e("REGISTER", e.message.toString())
                Log.d(
                    "REGISTER",
                    e.response()?.errorBody()?.string() ?: e.message().toString()
                )
            } catch (e: Exception) {
                Log.d("REGISTER", e.message.toString())
            }
        }
    }


    /**
     * Validates the input and updates the UI state with any errors.
     * It checks if the username length is between 4 and 10 characters, if the password and confirmed password match, and if the password length is greater than 8.
     */
    private fun validate() {
        _registerUiState.update { currentState ->
            currentState.copy(errors = emptyList())
        }

        if (_registerUiState.value.username.length !in 4..10) {
            _registerUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Username length should be between 4 to 10 characters."
                )
            }
        }

        if (_registerUiState.value.password != _registerUiState.value.confirmPassword) {
            _registerUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Passwords must match."
                )
            }
        }

        if (_registerUiState.value.password.length < 8) {
            _registerUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Password length must be greater than 8."
                )
            }
        }
    }

    /**
     * A factory for creating instances of the RegisterViewModel class.
     * It uses the application container to get instances of UserRespository and AccessTokenPreferencesRepository.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChattyApplication)
                RegisterViewModel(
                    application.container.userRespository,
                    application.container.accessTokenPreferencesRepository
                )
            }
        }
    }
}