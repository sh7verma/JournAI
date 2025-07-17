package com.shverma.app.ui.journalentry

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.utils.UiEvent

class JournalEntryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JournalEntryUiState())
    val uiState: StateFlow<JournalEntryUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set
}

data class JournalEntryUiState(
    val entryText: String = "",
    val selectedMood: Mood? = null,
    val prompt: String = "Reflect on your day and how you felt."
)
