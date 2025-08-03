package com.shverma.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.repository.AuthRepository
import com.shverma.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val isSocialLoginInProgress: Boolean = false,
    val socialLoginType: SocialLoginType? = null,
    val error: String? = null
)

enum class SocialLoginType {
    GOOGLE, APPLE
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                isLoginEnabled = email.isNotBlank() && it.password.isNotBlank(),
                error = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isLoginEnabled = it.email.isNotBlank() && password.isNotBlank(),
                error = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = authRepository.login(state.email, state.password)) {
                is Resource.Success -> {
                    val token = result.data
                    if (token != null) {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        onSuccess()
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Unknown error") }
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        _uiState.update { it.copy(isLoading = true, isRegistering = true, error = null) }
        viewModelScope.launch {
            when (val result = authRepository.register(state.email, state.password)) {
                is Resource.Success -> {
                    val token = result.data
                    if (token != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistering = false,
                                error = null
                            )
                        }
                        onSuccess()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistering = false,
                                error = "Unknown error"
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistering = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun loginWithGoogle(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        _uiState.update { 
            it.copy(
                isLoading = true, 
                isSocialLoginInProgress = true,
                socialLoginType = SocialLoginType.GOOGLE,
                error = null
            ) 
        }

        // This is a placeholder for the actual Google authentication implementation
        // In a real implementation, you would:
        // 1. Launch the Google Sign-In intent
        // 2. Handle the result in the activity/fragment
        // 3. Send the Google ID token to your backend
        // 4. Get back a session token

        // For now, we'll just simulate a delay and success
        viewModelScope.launch {
            // Simulate network delay
            kotlinx.coroutines.delay(1500)

            // Update UI state to indicate success and call the onSuccess callback
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    isSocialLoginInProgress = false,
                    socialLoginType = null,
                    error = null
                ) 
            }
            onSuccess()
        }
    }

    fun loginWithApple(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        _uiState.update { 
            it.copy(
                isLoading = true, 
                isSocialLoginInProgress = true,
                socialLoginType = SocialLoginType.APPLE,
                error = null
            ) 
        }

        // This is a placeholder for the actual Apple authentication implementation
        // In a real implementation, you would:
        // 1. Launch the Apple Sign-In flow
        // 2. Handle the result in the activity/fragment
        // 3. Send the Apple ID token to your backend
        // 4. Get back a session token

        // For now, we'll just simulate a delay and success
        viewModelScope.launch {
            // Simulate network delay
            kotlinx.coroutines.delay(1500)

            // Update UI state to indicate success and call the onSuccess callback
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    isSocialLoginInProgress = false,
                    socialLoginType = null,
                    error = null
                ) 
            }
            onSuccess()
        }
    }
}
