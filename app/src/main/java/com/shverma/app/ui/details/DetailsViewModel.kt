package com.shverma.app.ui.details

import androidx.lifecycle.ViewModel
import com.shverma.app.utils.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.threeten.bp.LocalDate

class DetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        DetailsUiState(
            journalEntries = mapOf(
                LocalDate.now() to "Today was quite productive. I managed to complete my presentation for tomorrow's meeting ahead of schedule. The weather was perfect for an evening walk in the park, which helped clear my mind. Feeling optimistic about the week ahead.",
                LocalDate.now().minusDays(1) to "Yesterday was a bit stressful, but I managed to get through it with some meditation.",
                // ...add more entries as needed...
            )
        )
    )
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set
}

data class DetailsUiState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val moodLabel: String = "",
    val journalEntries: Map<LocalDate, String> = emptyMap()
)
