package com.shverma.app.ui.details

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
import com.shverma.androidstarter.R
import com.shverma.app.ui.DetailedJournalCard
import com.shverma.app.ui.customViews.CalendarWeekRow
import com.shverma.app.ui.customViews.createMoodMap
import com.shverma.app.ui.customViews.generateCalendarDays
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.utils.UiEvent
import com.shverma.app.utils.toOffsetDateTime
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
            selectedDate = state.selectedDate ?: startDate,
            onDateSelected = {
                detailsViewModel.onDateSelected(it)
            }
        )

        Spacer(Modifier.height(20.dp))

        // Multiple Journal Entry Cards
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
                    onGetAiTips = onGetAiTips
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
    }
}
