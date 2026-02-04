package com.shverma.journai.ui.details

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shverma.journai.R
import com.shverma.journai.ui.DetailedJournalCard
import com.shverma.journai.ui.customViews.CalendarWeekRow
import com.shverma.journai.ui.customViews.createMoodMap
import com.shverma.journai.ui.customViews.generateCalendarDays
import com.shverma.journai.ui.theme.AppTypography
import com.shverma.journai.ui.theme.JournAIBackground
import com.shverma.journai.ui.theme.JournAIBrown
import com.shverma.journai.ui.theme.dimensions
import com.shverma.journai.utils.UiEvent
import com.shverma.journai.utils.toOffsetDateTime
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun DetailScreen(
    date: String,
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onGetAiTips: () -> Unit = {},
    detailsViewModel: DetailsViewModel = hiltViewModel<DetailsViewModel>()
) {
    val context = LocalContext.current
    val state by detailsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(date) {
        detailsViewModel.onDateSelected(date.toOffsetDateTime())
    }

    LaunchedEffect(true) {
        detailsViewModel.uiEvent.receiveAsFlow().collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                else -> Unit
            }
        }
    }

    // Use OffsetDateTime directly with CalendarWeekRow
    val startDate = state.startDate
    val endDate = state.endDate

    val moodMap = createMoodMap(startDate, endDate)

    val days = remember(startDate, endDate, moodMap) {
        generateCalendarDays(startDate, endDate, moodMap)
    }
    // --- Main Layout ---
    val dims = dimensions()
    Column(
        modifier = Modifier
            .background(JournAIBackground)
            .padding(horizontal = dims.spacingRegular)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Back button & title
        Spacer(Modifier.height(dims.spacingMedium))
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

        Spacer(Modifier.height(dims.spacingMedium))

        // Calendar
        CalendarWeekRow(
            days = days,
            selectedDate = state.selectedDate ?: startDate,
            onDateSelected = {
                detailsViewModel.onDateSelected(it)
            }
        )

        Spacer(Modifier.height(dims.spacingLarge))

        // Multiple Journal Entry Cards
        if (state.journalEntries.isEmpty()) {
            Card(
                shape = RoundedCornerShape(dims.radiusLarge),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dims.spacingXXSmall),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = dims.elevationSmall)
            ) {
                Column(Modifier.padding(dims.spacingRegular)) {
                    Text(
                        text = context.getString(R.string.no_entry_for_date),
                        style = AppTypography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        } else {
            state.journalEntries.forEach { entry ->
                DetailedJournalCard(
                    entry = entry,
                    tips = entry.aiTips,
                    grammarCorrection = entry.grammarCorrection,
                    onGetAiTips = { detailsViewModel.getAiTips(entry) },
                    onGetGrammarCorrection = { detailsViewModel.getGrammarCorrection(entry) },
                    isLoadingTips = state.loadingTipsForEntryId == entry.id,
                    isLoadingGrammar = state.loadingGrammarForEntryId == entry.id
                )
            }
        }

        Spacer(modifier = Modifier.height(dims.spacingXLarge))
    }
}
