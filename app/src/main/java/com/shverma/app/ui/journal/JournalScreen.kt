package com.shverma.app.ui.journal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.receiveAsFlow
import com.shverma.app.utils.UiEvent

@Composable
fun JournalScreen(
    snackBarHostState: SnackbarHostState,
    journalViewModel: JournalViewModel = viewModel()
) {
    val state by journalViewModel.uiState.collectAsStateWithLifecycle()
    androidx.compose.runtime.LaunchedEffect(true) {
        journalViewModel.uiEvent.receiveAsFlow().collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }
                else -> Unit
            }
        }
    }

    // ...existing UI code using 'state'...
}

