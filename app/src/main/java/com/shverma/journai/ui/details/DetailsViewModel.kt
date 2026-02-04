package com.shverma.journai.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.journai.R
import com.shverma.journai.data.network.model.JournalDetail
import com.shverma.journai.data.repository.AiRepository
import com.shverma.journai.data.repository.JournalRepository
import com.shverma.journai.utils.GlobalResourceProvider
import com.shverma.journai.utils.Resource
import com.shverma.journai.utils.UiEvent
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
class DetailsViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DetailsUiState()
    )
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    fun onDateSelected(date: OffsetDateTime) {
        _uiState.update { it.copy(selectedDate = date) }
        fetchJournalForDate(date)
    }

    private fun fetchJournalForDate(date: OffsetDateTime) {
        viewModelScope.launch {
            when (val result = journalRepository.getEntriesByDate(date)) {
                is Resource.Success -> {
                    val entries = result.data?.entries ?: emptyList()
                    // Sort entries by created_at in descending order (latest first)
                    val sortedEntries = entries.sortedByDescending { it.created_at }
                    val startDate = result.data?.startDate ?: date
                    _uiState.update { state ->
                        state.copy(
                            journalEntries = sortedEntries,
                            selectedDate = date,
                            startDate = startDate
                        )
                    }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message))
                    _uiState.update { state ->
                        state.copy(
                            journalEntries = emptyList(),
                            selectedDate = date
                        )
                    }
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
        // Set loading state for this entry
        _uiState.update { it.copy(loadingTipsForEntryId = journalEntry.id) }

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
                                    },
                                    loadingTipsForEntryId = null // Clear loading state
                                )
                            }
                        }
                }

                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowMessage(result.message ?: GlobalResourceProvider.getGlobalString(R.string.error_failed_ai_tips)))
                    _uiState.update { it.copy(loadingTipsForEntryId = null) } // Clear loading state on error
                }
            }
        }
    }

    fun getGrammarCorrection(journalEntry: JournalDetail) {
        // Set loading state for this entry
        _uiState.update { it.copy(loadingGrammarForEntryId = journalEntry.id) }

        viewModelScope.launch {
            when (val result = aiRepository.correctGrammar(journalEntry.text, journalEntry.id)) {
                is Resource.Success -> {
                    val correctedText = result.data?.corrected ?: ""
                    uiState.value.journalEntries
                        .find { it.id == journalEntry.id }?.let { entry ->
                            _uiState.update { state ->
                                state.copy(
                                    journalEntries = state.journalEntries.map {
                                        if (it.id == entry.id) {
                                            it.copy(grammarCorrection = correctedText)
                                        } else {
                                            it
                                        }
                                    },
                                    loadingGrammarForEntryId = null // Clear loading state
                                )
                            }
                        }
                }

                is Resource.Error -> {
                    sendUiEvent(
                        UiEvent.ShowMessage(
                            result.message ?: GlobalResourceProvider.getGlobalString(R.string.error_failed_grammar_correction)
                        )
                    )
                    _uiState.update { it.copy(loadingGrammarForEntryId = null) } // Clear loading state on error
                }
            }
        }
    }
}

data class DetailsUiState(
    val startDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val endDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val selectedDate: OffsetDateTime? = null,
    val moodLabel: String = "",
    val journalEntries: List<JournalDetail> = emptyList(),
    val loadingTipsForEntryId: String? = null,
    val loadingGrammarForEntryId: String? = null
)
