package com.shverma.journai.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.journai.data.repository.AuthRepository
import com.shverma.journai.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
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


    fun loginWithGoogle(
        idToken: String,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isLoading) return
        _uiState.update {
            it.copy(isLoading = true, error = null)
        }
        viewModelScope.launch {
            when (val result =
                authRepository.signInWithGoogle(idToken)
            ) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    onSuccess()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
    }


    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}
