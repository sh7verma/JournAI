package com.shverma.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.preference.DataStoreHelper
import com.shverma.app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            dataStoreHelper.userEmail.collectLatest { email ->
                email?.let {
                    _uiState.value = _uiState.value.copy(
                        email = it,
                        userName = it.substringBefore('@') // Use part of email as username
                    )
                }
            }
        }

        viewModelScope.launch {
            dataStoreHelper.streak.collectLatest { streak ->
                _uiState.value = _uiState.value.copy(
                    streak = streak ?: 0
                )
            }
        }

        viewModelScope.launch {
            dataStoreHelper.mood.collectLatest { mood ->
                _uiState.value = _uiState.value.copy(
                    mood = mood ?: ""
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            dataStoreHelper.clearAllData()
            // Navigate to login screen would be handled by the caller
        }
    }
}

data class ProfileUiState(
    val userName: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val streak: Int = 0,
    val mood: String = ""
)
