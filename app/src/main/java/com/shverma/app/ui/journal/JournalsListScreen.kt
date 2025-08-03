package com.shverma.app.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shverma.androidstarter.R
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.ui.DetailedJournalCard
import com.shverma.app.ui.customViews.CalendarWeekRow
import com.shverma.app.ui.customViews.createMoodMap
import com.shverma.app.ui.customViews.generateCalendarDays
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.utils.UiEvent
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun JournalsListScreen(
    snackBarHostState: SnackbarHostState,
    journalViewModel: JournalsListViewModel = hiltViewModel<JournalsListViewModel>()
) {
    val state by journalViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        journalViewModel.uiEvent.receiveAsFlow().collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                else -> Unit
            }
        }
    }

    val startDate = state.startDate
    val endDate = state.endDate
    val selectedDate = state.selectedDate

    // Use endDate as fallback if startDate is null
    val effectiveStartDate = startDate ?: endDate
    val moodMap = createMoodMap(effectiveStartDate, endDate)
    val days = remember(startDate, endDate, moodMap) {
        generateCalendarDays(effectiveStartDate, endDate, moodMap)
    }

    Column(
        modifier = Modifier
            .background(JournAIBackground)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Back button & title
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = AppTypography.titleLarge,
                color = JournAIBrown,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Calendar
        CalendarWeekRow(
            days = days,
            selectedDate = selectedDate,
            onDateSelected = { journalViewModel.onDateSelected(it) }
        )

        Spacer(Modifier.height(20.dp))

        // Journal Entries for selected date
        if (state.journalEntries.isEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.no_journal_entries_for_this_date),
                        style = AppTypography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        } else {
            state.journalEntries.forEach { entry: JournalDetail ->
                DetailedJournalCard(
                    entry = entry,
                    tips = entry.aiTips,
                    grammarCorrection = entry.grammarCorrection,
                    onGetAiTips = { journalViewModel.getAiTips(entry) },
                    onGetGrammarCorrection = { journalViewModel.getGrammarCorrection(entry) }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
    }
}
