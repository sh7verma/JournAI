package com.shverma.app.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.androidstarter.R
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.repository.AiRepository
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.utils.GlobalResourceProvider
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class JournalsListViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DetailsUiState()
    )
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    init {
        fetchEntriesForDate(OffsetDateTime.now(ZoneOffset.UTC))
    }

    fun onDateSelected(date: OffsetDateTime) {
        _uiState.update { it.copy(selectedDate = date) }
        fetchEntriesForDate(date)
    }

    private fun fetchEntriesForDate(date: OffsetDateTime) {
        viewModelScope.launch {
            when (val result = journalRepository.getEntriesByDate(date)) {
                is Resource.Success -> {
                    val response = result.data
                    // Sort entries by created_at in descending order (latest first)
                    val sortedEntries = (response?.entries ?: emptyList()).sortedByDescending { it.created_at }
                    _uiState.update { state ->
                        state.copy(
                            journalEntries = sortedEntries,
                            startDate = response?.startDate ?: date,
                        )
                    }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message))
                    _uiState.update { state -> state.copy(journalEntries = emptyList()) }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEvent.send(event)
        }
    }

    fun getAiTips(journalEntry: JournalDetail) {
        viewModelScope.launch {
            when (val result = aiRepository.getTips(journalEntry.text, journalEntry.id)) {
                is Resource.Success -> {
                    val tips = result.data?.tips ?: emptyList()
                    uiState.value.journalEntries
                        .find { it.id == journalEntry.id }?.let { entry ->
                            _uiState.update { state ->
                                state.copy(
                                    journalEntries = state.journalEntries.map {
                                        if (it.id == entry.id) {
                                            it.copy(aiTips = tips)
                                        } else {
                                            it
                                        }
                                    }
                                )
                            }
                        }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message ?: GlobalResourceProvider.getGlobalString(R.string.error_failed_ai_tips)))
                }
            }
        }
    }
}

data class DetailsUiState(
    val startDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val endDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val selectedDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val moodLabel: String = "",
    val journalEntries: List<JournalDetail> = emptyList(),
    val aiTips: Map<String, List<String>> = emptyMap()
)
