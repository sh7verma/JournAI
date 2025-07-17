package com.shverma.app.ui.journal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shverma.app.utils.UiEvent

class JournalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set
}

data class JournalUiState(
    val entries: List<String> = emptyList()
)
