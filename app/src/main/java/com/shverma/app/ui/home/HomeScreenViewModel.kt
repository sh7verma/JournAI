package com.shverma.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.R
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.network.model.WeeklyMoodSummaryResponse
import com.shverma.app.data.preference.DataStoreHelper
import com.shverma.app.data.repository.AiRepository
import com.shverma.app.data.repository.JournalRepository
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

data class HomeUiState(
    val userName: String = "",
    val currentDate: String = "",
    val prompt: String = "",
    val recentEntries: List<JournalDetail> = emptyList(),
    val moodSummary: WeeklyMoodSummaryResponse = WeeklyMoodSummaryResponse()
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val aiRepository: AiRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    fun refreshHomeScreen() {
        viewModelScope.launch {
            val currentDate = getCurrentDateFormatted()
            val storedPromptDate = dataStoreHelper.promptDate.firstOrNull()
            val storedPrompt = dataStoreHelper.dailyPrompt.firstOrNull()
            val userEmail = dataStoreHelper.userEmail.firstOrNull()
            val storedMood = dataStoreHelper.mood.firstOrNull()
            val storedStreak = dataStoreHelper.streak.firstOrNull() ?: 0

            // Extract username from email (everything before @)
            val userName =
                userEmail?.split("@")?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: ""

            // Initialize UI state with stored mood summary if available
            _uiState.update { state ->
                state.copy(
                    userName = userName,
                    currentDate = currentDate,
                    moodSummary = WeeklyMoodSummaryResponse(
                        mood = storedMood,
                        streak = storedStreak
                    )
                )
            }

            // Fetch mood summary from network
            fetchMoodSummary()

            if (storedPrompt == null || storedPromptDate != currentDate) {
                when (val result = aiRepository.getPrompt()) {
                    is Resource.Success -> {
                        val newPrompt = result.data?.prompt
                        newPrompt?.let {
                            _uiState.update { state ->
                                state.copy(
                                    userName = userName,
                                    currentDate = currentDate,
                                    prompt = newPrompt
                                )
                            }
                            dataStoreHelper.saveDailyPrompt(it, currentDate)
                        }
                    }

                    is Resource.Error -> {
                        val promptToUse = storedPrompt ?: GlobalResourceProvider.getGlobalString(R.string.default_prompt)

                        _uiState.update { state ->
                            state.copy(
                                userName = userName,
                                currentDate = currentDate,
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
                        userName = userName,
                        currentDate = currentDate,
                        prompt = storedPrompt
                    )
                }
            }

            fetchJournalHistory()
        }
    }

    private fun fetchMoodSummary() {
        viewModelScope.launch {
            when (val result = journalRepository.getWeeklyMoodSummary()) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _uiState.update { state ->
                            state.copy(
                                moodSummary = WeeklyMoodSummaryResponse(
                                    streak = data.streak,
                                    mood = data.mood
                                )
                            )
                        }
                        // Save mood summary to DataStore
                        dataStoreHelper.saveMoodSummary(data.mood, data.streak)
                    }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message))
                }
            }
        }
    }

    private fun fetchJournalHistory() {
        viewModelScope.launch {
            when (val result = journalRepository.getHistory()) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            recentEntries = (result.data
                                ?: emptyList()).sortedByDescending { it.created_at }
                        )
                    }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message))
                }
            }
        }
    }


    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEvent.send(event)
        }
    }
}
