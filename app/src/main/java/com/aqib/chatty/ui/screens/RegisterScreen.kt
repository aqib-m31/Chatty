@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.aqib.chatty.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aqib.chatty.R
import com.aqib.chatty.ui.RegisterViewModel
import com.aqib.chatty.ui.theme.ChattyTheme

/**
 * Composable function for the Register Screen.
 * It displays a form for the user to enter their username and password and a button to register.
 * It also provides a link to navigate to the Login Screen.
 *
 * @param navigateLoginScreen A function to be called when the user clicks on the "Already Registered? Login" link.
 * @param authenticate A function to be called when the user successfully registers.
 * @param modifier A Modifier to be applied to the Column composable.
 */
@Composable
fun RegisterScreen(
    navigateLoginScreen: () -> Unit,
    authenticate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
    val registerUiState = registerViewModel.registerUiState.collectAsState().value
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.register),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )
        OutlinedTextField(
            value = registerUiState.username, onValueChange = registerViewModel::updateUsername,
            label = {
                Text(text = stringResource(R.string.username))
            },
            singleLine = true
        )
        OutlinedTextField(
            value = registerUiState.password, onValueChange = registerViewModel::updatePassword,
            label = {
                Text(text = stringResource(R.string.password))
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = registerUiState.confirmPassword,
            onValueChange = registerViewModel::updateConfirmPassword,
            label = {
                Text(text = stringResource(R.string.confirm_password))
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                Column {
                    for (error in registerUiState.errors) {
                        Text(text = error, color = Color.Red)
                    }
                }
            }
        )

        Button(
            onClick = { registerViewModel.register(authenticate) },
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(text = stringResource(R.string.create_account))
        }

        TextButton(onClick = navigateLoginScreen) {
            Text(text = "Already Registered? Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ChattyTheme {
        RegisterScreen({}, {})
    }
}