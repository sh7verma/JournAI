package com.shverma.app.ui.journalentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.R
import com.shverma.app.data.network.model.JournalEntryCreate
import com.shverma.app.data.preference.DataStoreHelper
import com.shverma.app.data.repository.AiRepository
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.utils.GlobalResourceProvider
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import com.shverma.app.utils.getCurrentDateFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalEntryViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val aiRepository: AiRepository,
    private val dataStoreHelper: DataStoreHelper
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


    init {
        initializeJournalEntry()
    }

    fun initializeJournalEntry() {
        viewModelScope.launch {
            val currentDate = getCurrentDateFormatted()

            val storedPromptDate = dataStoreHelper.promptDate.firstOrNull()
            val storedPrompt = dataStoreHelper.dailyPrompt.firstOrNull()

            if (storedPrompt == null || storedPromptDate != currentDate) {
                when (val result = aiRepository.getPrompt()) {
                    is Resource.Success -> {
                        val newPrompt = result.data?.prompt
                        newPrompt?.let {
                            _uiState.update { state ->
                                state.copy(
                                    prompt = newPrompt
                                )
                            }
                            dataStoreHelper.saveDailyPrompt(it, currentDate)
                        }
                    }

                    is Resource.Error -> {
                        val promptToUse = storedPrompt
                            ?: GlobalResourceProvider.getGlobalString(R.string.default_prompt)

                        _uiState.update { state ->
                            state.copy(
                                prompt = promptToUse
                            )
                        }
                        sendUiEvent(UiEvent.ShowMessage(result.message))
                    }
                }
            } else {
                // Use the stored prompt from today
                _uiState.update { state ->
                    state.copy(
                        prompt = storedPrompt
                    )
                }
            }
        }
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

    fun analyzeMoodInEntry() {
        val text = _uiState.value.entryText
        if (text.isBlank()) {
            sendUiEvent(UiEvent.ShowMessage("Please enter some text to analyze"))
            return
        }

        _uiState.update { it.copy(isAnalyzingSentiment = true) }

        viewModelScope.launch {
            when (val result = aiRepository.analyzeSentiment(text, null)) {
                is Resource.Success -> {
                    val analysis = result.data?.analysis ?: "No analysis available"
                    val moodString = result.data?.mood

                    // Convert mood string to Mood enum
                    val detectedMood = when (moodString) {
                        "Sad" -> Mood.Sad
                        "Down" -> Mood.Down
                        "Good" -> Mood.Good
                        "Great" -> Mood.Great
                        else -> null
                    }

                    _uiState.update {
                        it.copy(
                            sentimentAnalysis = analysis,
                            selectedMood = detectedMood,
                            isAnalyzingSentiment = false
                        )
                    }
                }

                is Resource.Error -> {
                    sendUiEvent(
                        UiEvent.ShowMessage(
                            result.message ?: "Failed to analyze sentiment"
                        )
                    )
                    _uiState.update { it.copy(isAnalyzingSentiment = false) }
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
    val prompt: String = "Reflect on your day and how you felt.",
    val sentimentAnalysis: String? = null,
    val isAnalyzingSentiment: Boolean = false
)
