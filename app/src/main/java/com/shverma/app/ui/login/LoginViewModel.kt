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
    val error: String? = null
)

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
}
