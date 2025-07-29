package com.shverma.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import com.shverma.app.utils.getCurrentDateFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MoodSummary(
    val streak: Int,
    val trend: String
)

data class HomeUiState(
    val userName: String = "",
    val currentDate: String = "",
    val prompt: String = "",
    val recentEntries: List<JournalDetail> = emptyList(),
    val moodSummary: MoodSummary = MoodSummary(0, "")
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    fun refreshHomeScreen() {
        _uiState.update { state ->
            state.copy(
                userName = "Sarah",
                currentDate = getCurrentDateFormatted(),
                prompt = "What made you smile today?",
                moodSummary = MoodSummary(5, "Mostly Positive")
            )
        }
        fetchJournalHistory()
    }

    private fun fetchJournalHistory() {
        viewModelScope.launch {
            when (val result = journalRepository.getHistory()) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            recentEntries = (result.data ?: emptyList()).sortedByDescending { it.created_at }
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
