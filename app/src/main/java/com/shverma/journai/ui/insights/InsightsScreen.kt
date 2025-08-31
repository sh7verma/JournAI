package com.shverma.journai.ui.insights

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.receiveAsFlow
import com.shverma.journai.utils.UiEvent

@Composable
fun InsightsScreen(
    snackBarHostState: SnackbarHostState,
    insightsViewModel: InsightsViewModel = viewModel()
) {
    val state by insightsViewModel.uiState.collectAsStateWithLifecycle()
    androidx.compose.runtime.LaunchedEffect(true) {
        insightsViewModel.uiEvent.receiveAsFlow().collect { event ->
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

