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
 * LoginUiState is a data class that represents the UI state of the login screen.
 *
 * @property username The username entered by the user. Default value is "Hello".
 * @property password The password entered by the user. Default value is "asdfghjkl".
 * @property errors A list of error messages. Default value is an empty list.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val errors: List<String> = emptyList(),
)

/**
 * LoginViewModel is a class that manages the UI state and business logic for the login screen.
 * It communicates with the UserRepository and AccessTokenPreferencesRepository to perform user login and store access tokens.
 * It exposes a state flow of LoginUiState for observing the UI state from the UI layer.
 * It provides methods for updating the username, password, and for performing the login operation.
 *
 * @property userRepository The repository for performing user-related operations.
 * @property accessTokenPreferencesRepository The repository for managing access tokens and usernames in a data store.
 */
class LoginViewModel(
    private val userRepository: UserRespository,
    private val accessTokenPreferencesRepository: AccessTokenPreferencesRepository
) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    /**
     * Updates the username in the UI state.
     *
     * @param username The new username.
     */
    fun updateUsername(username: String) {
        _loginUiState.update { currentState ->
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
        _loginUiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    /**
     * Performs the login operation.
     * It first validates the input. If the input is valid, it attempts to login the user.
     * If the login is successful, it stores the access token and username in the data store.
     * If the login is unsuccessful, it updates the UI state with the error message.
     * It catches any exceptions that might occur during the operation and logs them.
     *
     * @param authenticate A function to be called if the login is successful.
     */
    fun login(authenticate: () -> Unit) {
        validate()
        if (_loginUiState.value.errors.isEmpty()) {
            viewModelScope.launch {
                try {
                    val info = userRepository.login(
                        _loginUiState.value.username,
                        _loginUiState.value.password,
                    )
                    if (info.accessToken != null && info.username != null) {
                        accessTokenPreferencesRepository.setAccessToken(info.accessToken)
                        accessTokenPreferencesRepository.setUsername(info.username)
                    }

                    Log.d("LOGIN", info.toString())
                    if (!info.error) {
                        authenticate()
                    }
                    if (info.error) {
                        _loginUiState.update { currentState ->
                            currentState.copy(
                                errors = listOf(info.message)
                            )
                        }
                    }
                } catch (e: HttpException) {
                    _loginUiState.update { currentState ->
                        currentState.copy(
                            errors = listOf(e.response()?.errorBody()?.string() ?: "")
                        )
                    }
                    Log.e("LOGIN", e.message.toString())
                    Log.d(
                        "LOGIN",
                        e.response()?.errorBody()?.string() ?: e.message().toString()
                    )
                } catch (e: Exception) {
                    Log.d("LOGIN", e.message.toString())
                }
            }
        }
    }

    /**
     * Validates the input and updates the UI state with any errors.
     * It checks if the username and password are not empty.
     */
    private fun validate() {
        _loginUiState.update { currentState ->
            currentState.copy(errors = emptyList())
        }

        if (_loginUiState.value.username.isEmpty() && _loginUiState.value.password.isEmpty()) {
            _loginUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Username and password cannot be empty."
                )
            }
        } else if (_loginUiState.value.username.isEmpty()) {
            _loginUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Username cannot be empty."
                )
            }
        } else if (_loginUiState.value.password.isEmpty()) {
            _loginUiState.update { currentState ->
                currentState.copy(
                    errors = currentState.errors + "Password cannot be empty."
                )
            }
        }
    }


    /**
     * A factory for creating instances of the LoginViewModel class.
     * It uses the application container to get instances of UserRespository and AccessTokenPreferencesRepository.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChattyApplication)
                LoginViewModel(
                    application.container.userRespository,
                    application.container.accessTokenPreferencesRepository
                )
            }
        }
    }
}