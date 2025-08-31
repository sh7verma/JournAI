package com.shverma.app.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shverma.app.R
import com.shverma.app.ui.login.SocialLoginType
import com.shverma.app.ui.DottedLineSeparator
import com.shverma.app.ui.JournButton
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach
import com.shverma.app.ui.theme.dimensions

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = loginViewModel.uiState.collectAsStateWithLifecycle().value

    val dims = dimensions()
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dims.spacingXLarge),
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
                        .size(dims.buttonHeight + dims.spacingXLarge)
                        .clip(CircleShape)
                        .background(JournAILightPeach),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_journal_book),
                        contentDescription = "JournAI Logo",
                        modifier = Modifier.size(dims.iconSizeLarge + dims.iconSizeMedium),
                        tint = JournAIBrown
                    )
                }

                Spacer(modifier = Modifier.height(dims.spacingXXLarge))

                // Title
                Text(
                    text = stringResource(R.string.welcome_to_journai),
                    style = AppTypography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = JournAIBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dims.spacingSmall))

                // Subtitle
                Text(
                    text = stringResource(R.string.app_subtitle),
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Middle section with login form
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dims.spacingXXLarge)
            ) {
                // Email field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { loginViewModel.onEmailChange(it) },
                    placeholder = { Text(stringResource(R.string.email_placeholder)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "Email Icon",
                            tint = JournAIBrown
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(dims.radiusLarge),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JournAIBrown,
                        unfocusedBorderColor = JournAIBrown.copy(alpha = 0.5f),
                        focusedTextColor = JournAIBrown,
                        unfocusedTextColor = JournAIBrown,
                        cursorColor = JournAIBrown
                    )
                )

                Spacer(modifier = Modifier.height(dims.spacingRegular))

                // Password field with show/hide toggle
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { loginViewModel.onPasswordChange(it) },
                    placeholder = { Text(stringResource(R.string.password_placeholder)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Password Icon",
                            tint = JournAIBrown
                        )
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (uiState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { loginViewModel.onTogglePasswordVisibility() }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (uiState.isPasswordVisible)
                                    stringResource(R.string.hide_password)
                                else
                                    stringResource(R.string.show_password),
                                tint = JournAIBrown
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(dims.radiusLarge),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JournAIBrown,
                        unfocusedBorderColor = JournAIBrown.copy(alpha = 0.5f),
                        focusedTextColor = JournAIBrown,
                        unfocusedTextColor = JournAIBrown,
                        cursorColor = JournAIBrown
                    )
                )

                Spacer(modifier = Modifier.height(dims.spacingSmall))

                // Forgot password link
//                Text(
//                    text = stringResource(R.string.forgot_password),
//                    style = AppTypography.labelLarge,
//                    color = JournAIBrown,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { /* TODO: Implement forgot password functionality */ }
//                )

                Spacer(modifier = Modifier.height(dims.spacingXXLarge))

                // Login and Sign Up buttons vertically
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dims.spacingRegular)
                ) {
                    // Login button
                    JournButton(
                        text = stringResource(R.string.login),
                        backgroundColor = JournAIBrown,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = uiState.isLoading && !uiState.isRegistering,
                        onClick = {
                            if (uiState.isLoginEnabled && !uiState.isLoading) {
                                loginViewModel.login {
                                    onLoginSuccess()
                                }
                            }
                        }
                    )

                    // Sign Up button
                    JournButton(
                        text = stringResource(R.string.sign_up),
                        backgroundColor = JournAILightPeach,
                        contentColor = JournAIBrown,
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = uiState.isLoading && uiState.isRegistering,
                        onClick = {
                            if (uiState.isLoginEnabled && !uiState.isLoading) {
                                loginViewModel.register {
                                    onLoginSuccess()
                                }
                            }
                        }
                    )
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(dims.spacingSmall))
                    Text(
                        text = uiState.error ?: "",
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(dims.spacingXXLarge))

                // Divider with "or continue with" text
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    DottedLineSeparator(
//                        modifier = Modifier.weight(1f),
//                        color = JournAIBrown.copy(alpha = 0.5f)
//                    )
//                    Text(
//                        text = stringResource(R.string.or_continue_with),
//                        style = AppTypography.bodyMedium,
//                        color = JournAIBrown,
//                        modifier = Modifier.padding(horizontal = dims.spacingRegular)
//                    )
//                    DottedLineSeparator(
//                        modifier = Modifier.weight(1f),
//                        color = JournAIBrown.copy(alpha = 0.5f)
//                    )
//                }

                Spacer(modifier = Modifier.height(dims.spacingXXLarge))

                // Social login options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dims.spacingRegular)
                ) {
                    // Google login button
//                    if (uiState.isLoading && uiState.isSocialLoginInProgress && uiState.socialLoginType == SocialLoginType.GOOGLE) {
                        // Show loading indicator for Google login
//                        JournButton(
//                            text = "",
//                            iconResId = R.drawable.ic_google,
//                            backgroundColor = Color.White,
//                            contentColor = JournAIBrown,
//                            modifier = Modifier
//                                .weight(1f),
//                            isLoading = true,
//                            onClick = { }
//                        )
//                    } else {
                        // Show Google login button
//                        JournButton(
//                            text = "",
//                            iconResId = R.drawable.ic_google,
//                            backgroundColor = Color.White,
//                            contentColor = JournAIBrown,
//                            modifier = Modifier
//                                .weight(1f),
//                            onClick = {
//                                if (!uiState.isLoading) {
//                                    loginViewModel.loginWithGoogle {
//                                        onLoginSuccess()
//                                    }
//                                }
//                            }
//                        )
//                    }

                    // Apple login button
//                    if (uiState.isLoading && uiState.isSocialLoginInProgress && uiState.socialLoginType == SocialLoginType.APPLE) {
                        // Show loading indicator for Apple login
//                        JournButton(
//                            text = "",
//                            iconResId = R.drawable.ic_apple,
//                            backgroundColor = Color.White,
//                            contentColor = JournAIBrown,
//                            modifier = Modifier
//                                .weight(1f),
//                            isLoading = true,
//                            onClick = { }
//                        )
//                    } else {
                        // Show Apple login button
//                        JournButton(
//                            text = "",
//                            iconResId = R.drawable.ic_apple,
//                            backgroundColor = Color.White,
//                            contentColor = JournAIBrown,
//                            modifier = Modifier
//                                .weight(1f),
//                            onClick = {
//                                if (!uiState.isLoading) {
//                                    loginViewModel.loginWithApple {
//                                        onLoginSuccess()
//                                    }
//                                }
//                            }
//                        )
//                    }
                }
            }

            // Bottom section with footer text
            Box(
                modifier = Modifier.padding(bottom = dims.spacingRegular)
            ) {
                Text(
                    text = stringResource(R.string.motivational_quote),
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
