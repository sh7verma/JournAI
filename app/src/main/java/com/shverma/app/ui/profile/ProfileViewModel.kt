package com.shverma.app.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shverma.app.utils.UiEvent

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set
}

data class ProfileUiState(
    val userName: String = "",
    val email: String = "",
    val avatarUrl: String? = null
)
