package com.shverma.app.ui.journalentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.network.model.JournalEntryCreate
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalEntryViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(JournalEntryUiState())
    val uiState: StateFlow<JournalEntryUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    fun onTextChange(newText: String) {
        _uiState.update { it.copy(entryText = newText) }
    }

    fun onMoodSelected(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun createJournalEntry() {
        val text = _uiState.value.entryText
        val mood = _uiState.value.selectedMood?.label ?: ""
        if (text.isBlank() || mood.isBlank()) {
            sendUiEvent(UiEvent.ShowMessage("Please enter text and select a mood"))
            return
        }
        viewModelScope.launch {
            when (val result = journalRepository.createEntry(JournalEntryCreate(text, mood))) {
                is Resource.Success -> {
                    sendUiEvent(UiEvent.NavigateUp(null))
                    sendUiEvent(UiEvent.ShowMessage("Journal entry saved!"))
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message))
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEvent.send(event)
        }
    }
}

data class JournalEntryUiState(
    val entryText: String = "",
    val selectedMood: Mood? = null,
    val prompt: String = "Reflect on your day and how you felt."
)
