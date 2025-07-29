package com.shverma.app.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = loginViewModel.uiState.collectAsStateWithLifecycle().value

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo or name
            Text(
                text = "JournAI",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Email field
            TextField(
                value = uiState.email,
                onValueChange = { loginViewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password field with show/hide toggle
            TextField(
                value = uiState.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (uiState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { loginViewModel.onTogglePasswordVisibility() }) {
                        Icon(imageVector = image, contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Login button with loading state
            Button(
                onClick = {
                    loginViewModel.login {
                        onLoginSuccess()
                    }
                },
                enabled = uiState.isLoginEnabled && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading && uiState.isRegistering.not()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Register button with loading state
            OutlinedButton(
                onClick = {
                    loginViewModel.register {
                        onLoginSuccess()
                    }
                },
                enabled = uiState.isLoginEnabled && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading && uiState.isRegistering) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Register")
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
