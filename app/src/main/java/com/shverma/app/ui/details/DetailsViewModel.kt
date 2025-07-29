package com.shverma.app.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.repository.JournalRepository
import com.shverma.app.utils.Resource
import com.shverma.app.utils.UiEvent
import com.shverma.app.utils.toOffsetDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DetailsUiState()
    )
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set

    init {
        fetchJournalForDate(OffsetDateTime.now(ZoneOffset.UTC))
    }

    fun onDateSelected(date: LocalDate) {
        onDateSelected(date.toOffsetDateTime())
    }

    fun onDateSelected(date: OffsetDateTime) {
        fetchJournalForDate(date)
    }

    private fun fetchJournalForDate(date: OffsetDateTime) {
        viewModelScope.launch {
            when (val result = journalRepository.getEntriesByDate(date)) {
                is Resource.Success -> {
                    val entries = result.data?.entries ?: emptyList()
                    val startDate = result.data?.startDate ?: date
                    _uiState.update { state ->
                        state.copy(
                            journalEntries = entries,
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
}

data class DetailsUiState(
    val startDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val endDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val selectedDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val moodLabel: String = "",
    val journalEntries: List<JournalDetail> = emptyList()
)
