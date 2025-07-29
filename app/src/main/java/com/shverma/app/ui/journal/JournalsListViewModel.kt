package com.shverma.app.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import com.shverma.app.utils.toOffsetDateTime
import javax.inject.Inject

@HiltViewModel
class JournalsListViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DetailsUiState()
    )
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    init {
        fetchEntriesForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        fetchEntriesForDate(date)
    }

    private fun fetchEntriesForDate(date: LocalDate) {
        viewModelScope.launch {
            when (val result = journalRepository.getEntriesByDate(date.toOffsetDateTime())) {
                is Resource.Success -> {
                    val response = result.data
                    _uiState.update { state ->
                        state.copy(
                            journalEntries = response?.entries ?: emptyList(),
                            startDate = response?.startDate?.let {
                                try {
                                    it.toLocalDate().minusDays(30)
                                } catch (e: Exception) {
                                    state.startDate
                                }
                            } ?: state.startDate
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
}

data class DetailsUiState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val moodLabel: String = "",
    val journalEntries: List<JournalDetail> = emptyList()
)
