package com.shverma.app.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class JournalEntry(
    val date: String,
    val mood: String,
    val preview: String
)

data class MoodSummary(
    val streak: Int,
    val trend: String
)

data class HomeUiState(
    val userName: String = "",
    val currentDate: String = "",
    val prompt: String = "",
    val recentEntries: List<JournalEntry> = emptyList(),
    val moodSummary: MoodSummary = MoodSummary(0, "")
)


@HiltViewModel
class JournAIHomeViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    init {
        loadInitialData()
    }

    fun refreshHomeScreen() {
        loadInitialData()
        sendUiEvent(UiEvent.ShowMessage("Home screen refreshed"))
    }

    fun onEvent(event: JournAIHomeEvents) {
        when (event) {
            is JournAIHomeEvents.StartWriting -> {
                sendUiEvent(UiEvent.ShowMessage("Start Writing clicked"))
            }

            is JournAIHomeEvents.StartVoiceEntry -> {
                sendUiEvent(UiEvent.ShowMessage("Voice Entry clicked"))
            }
        }
    }

    private fun loadInitialData() {
        _uiState.update { state ->
            state.copy(
                userName = "Sarah",
                currentDate = getCurrentDateFormatted(),
                prompt = "What made you smile today?",
                recentEntries = sampleRecentEntries(),
                moodSummary = MoodSummary(5, "Mostly Positive")
            )
        }
    }

    @SuppressLint("NewApi")
    private fun getCurrentDateFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        return LocalDate.now().format(formatter)
    }

    private fun sampleRecentEntries(): List<JournalEntry> {
        return listOf(
            JournalEntry(
                "Feb 22",
                "😊 Positive",
                "Today was a productive day at work. I managed to complete..."
            ),
            JournalEntry(
                "Feb 21",
                "😊 Happy",
                "Had a wonderful coffee chat with Sarah this morning..."
            ),
            JournalEntry(
                "Feb 20",
                "😊 Grateful",
                "Feeling grateful for the small moments today..."
            )
        )
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEvent.send(event)
        }
    }
}

sealed interface JournAIHomeEvents {
    object StartWriting : JournAIHomeEvents
    object StartVoiceEntry : JournAIHomeEvents
}
