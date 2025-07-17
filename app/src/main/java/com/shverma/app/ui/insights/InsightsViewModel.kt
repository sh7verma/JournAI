package com.shverma.app.ui.insights

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shverma.app.utils.UiEvent

class InsightsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    var uiEvent = Channel<UiEvent>()
        private set
}

data class InsightsUiState(
    val insights: List<String> = emptyList()
)
