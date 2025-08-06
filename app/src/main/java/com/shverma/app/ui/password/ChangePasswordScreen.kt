package com.shverma.app.ui.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shverma.app.R
import com.shverma.app.ui.JournButton
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach

@Composable
fun ChangePasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Back button at the top left
        IconButton(
            onClick = { onBackToLogin() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = JournAIBrown
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with logo, title, and subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular card with journaling/book icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(JournAILightPeach),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_journal_book),
                        contentDescription = "JournAI Logo",
                        modifier = Modifier.size(40.dp),
                        tint = JournAIBrown
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Change Password",
                    style = AppTypography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = JournAIBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Change your password to keep your account secure.",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Middle section with form
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Current Password field with show/hide toggle
                OutlinedTextField(
                    value = uiState.currentPassword,
                    onValueChange = { viewModel.onCurrentPasswordChange(it) },
                    placeholder = { Text("Enter current password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Current Password Icon",
                            tint = JournAIBrown
                        )
                    },
                    visualTransformation = if (uiState.isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (uiState.isCurrentPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { viewModel.onToggleCurrentPasswordVisibility() }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (uiState.isCurrentPasswordVisible)
                                    stringResource(R.string.hide_password)
                                else
                                    stringResource(R.string.show_password),
                                tint = JournAIBrown
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JournAIBrown,
                        unfocusedBorderColor = JournAIBrown.copy(alpha = 0.5f),
                        focusedTextColor = JournAIBrown,
                        unfocusedTextColor = JournAIBrown,
                        cursorColor = JournAIBrown
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New Password field with show/hide toggle
                OutlinedTextField(
                    value = uiState.newPassword,
                    onValueChange = { viewModel.onNewPasswordChange(it) },
                    placeholder = { Text("Enter new password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Password Icon",
                            tint = JournAIBrown
                        )
                    },
                    visualTransformation = if (uiState.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (uiState.isNewPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { viewModel.onToggleNewPasswordVisibility() }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (uiState.isNewPasswordVisible)
                                    "Hide password"
                                else
                                    "Show password",
                                tint = JournAIBrown
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JournAIBrown,
                        unfocusedBorderColor = JournAIBrown.copy(alpha = 0.5f),
                        focusedTextColor = JournAIBrown,
                        unfocusedTextColor = JournAIBrown,
                        cursorColor = JournAIBrown
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm New Password field with show/hide toggle
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    placeholder = { Text("Confirm new password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Password Icon",
                            tint = JournAIBrown
                        )
                    },
                    visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (uiState.isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { viewModel.onToggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (uiState.isConfirmPasswordVisible)
                                    "Hide password"
                                else
                                    "Show password",
                                tint = JournAIBrown
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JournAIBrown,
                        unfocusedBorderColor = JournAIBrown.copy(alpha = 0.5f),
                        focusedTextColor = JournAIBrown,
                        unfocusedTextColor = JournAIBrown,
                        cursorColor = JournAIBrown
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error message if any
                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        style = AppTypography.bodyMedium,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                JournButton(
                    text = "Change Password",
                    backgroundColor = if (uiState.isChangePasswordEnabled) JournAIBrown else JournAIBrown.copy(
                        alpha = 0.5f
                    ),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading,
                    onClick = {
                        if (uiState.isChangePasswordEnabled) {
                            viewModel.changePassword {
                                onBackToLogin()
                            }
                        }
                    }
                )

            }

            // Bottom section with back to login link and footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Back to Login link
                Text(
                    text = "Back to Login",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onBackToLogin() }
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Footer text
                Text(
                    text = "Take control of your security",
                    style = AppTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = JournAIBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
