package com.shverma.journai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shverma.journai.R
import com.shverma.journai.ui.JournButton
import com.shverma.journai.ui.JournalCard
import com.shverma.journai.ui.MoodSummaryCard
import com.shverma.journai.ui.PromptCard
import com.shverma.journai.ui.theme.AppTypography
import com.shverma.journai.ui.theme.JournAIBackground
import com.shverma.journai.ui.theme.JournAIBrown
import com.shverma.journai.ui.theme.JournAIPink
import com.shverma.journai.ui.theme.dimensions
import com.shverma.journai.utils.UiEvent
import com.shverma.journai.utils.toIsoString
import kotlinx.coroutines.flow.receiveAsFlow


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState,
    onClickEntry: (String) -> Unit,
    onStartWriting: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.uiEvent.receiveAsFlow().collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                else -> Unit
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshHomeScreen()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val dims = dimensions()

    Column(
        modifier = Modifier
            .background(JournAIBackground)
            .padding(horizontal = dims.spacingRegular)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Spacer(modifier = Modifier.height(dims.spacingSmall + dims.spacingXXSmall))
        Text(
            text = context.getString(R.string.good_morning, state.userName),
            style = AppTypography.titleLarge,
            color = JournAIBrown
        )
        Spacer(modifier = Modifier.height(dims.spacingXXSmall))
        Text(
            text = state.currentDate,
            style = AppTypography.bodyMedium,
            color = JournAIBrown
        )

        Spacer(modifier = Modifier.height(dims.spacingXLarge + dims.spacingXXSmall))

        // Today's Prompt
        PromptCard(
            title = context.getString(R.string.todays_prompt_quote),
            content = state.prompt,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.spacingLarge + dims.spacingXXSmall))

        // Buttons side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spacingRegular)
        ) {
            // Start Writing
            JournButton(
                text = stringResource(R.string.start_writing),
                iconResId = R.drawable.ic_write,
                backgroundColor = JournAIBrown,
                contentColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = { onStartWriting() }
            )

            // Voice Entry
            JournButton(
                text = stringResource(R.string.voice_entry),
                iconResId = R.drawable.ic_record,
                backgroundColor = JournAIPink,
                contentColor = JournAIBrown,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.onVoiceEntryClick() }
            )
        }

        Spacer(modifier = Modifier.height(dims.spacingMedium))

        // Weekly Mood Summary Card
        MoodSummaryCard(
            streakText = stringResource(R.string.day_streak, state.moodSummary.streak!!),
            mood = state.moodSummary.mood,
            onClick = {
                // Optional → navigate to detailed mood summary screen
            }
        )

        Spacer(modifier = Modifier.height(dims.spacingXXLarge))

        // Recent Entries
        Text(
            text = stringResource(R.string.recent_entries),
            style = AppTypography.titleMedium,
            color = JournAIBrown
        )
        Spacer(modifier = Modifier.height(dims.spacingSmall + dims.spacingXXSmall))

        if (state.recentEntries.isEmpty()) {
            Text(
                text = stringResource(R.string.no_entries_available),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            state.recentEntries.forEach { entry ->
                JournalCard(
                    entry = entry,
                    onClick = {
                        onClickEntry(it.toIsoString())
                    }
                )
                Spacer(modifier = Modifier.height(dims.spacingMedium))
            }
        }
        Spacer(modifier = Modifier.height(dims.spacingRegular))
    }
}
