package com.shverma.app.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shverma.androidstarter.R
import com.shverma.app.ui.JournButton
import com.shverma.app.ui.customViews.CalendarWeekRow
import com.shverma.app.ui.customViews.createMoodMap
import com.shverma.app.ui.customViews.generateCalendarDays
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAIPink
import org.threeten.bp.LocalDate
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.receiveAsFlow
import com.shverma.app.utils.UiEvent

@Composable
fun DetailScreen(
    itemId: Int,
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onGetAiTips: () -> Unit = {},
    detailsViewModel: DetailsViewModel = viewModel()
) {
    val state by detailsViewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(true) {
        detailsViewModel.uiEvent.receiveAsFlow().collect { event ->
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

    val moodMap = createMoodMap(startDate, endDate)

    val days = remember(startDate, endDate, moodMap) {
        generateCalendarDays(startDate, endDate, moodMap)
    }
    var selectedDate by remember { mutableStateOf(endDate) }

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
                text = "JournAI",
                style = AppTypography.titleLarge,
                color = JournAIBrown,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Month & year
        Text(
            text = "${selectedDate.dayOfMonth} ${
                selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            } ${selectedDate.year}",
            style = AppTypography.titleMedium,
            color = JournAIBrown,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(10.dp))

        // Calendar
        CalendarWeekRow(
            days = days,
            selectedDate = selectedDate,
            specialDate = null,
            onDateSelected = { selectedDate = it }
        )

        Spacer(Modifier.height(20.dp))

        // Journal Entry Card
        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = moodMap[selectedDate]?.icon
                            ?: Icons.Outlined.SentimentSatisfied,
                        contentDescription = moodMap[selectedDate]?.label ?: "",
                        tint = JournAIBrown,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = moodMap[selectedDate]?.label ?: "Okay",
                        style = AppTypography.labelLarge,
                        color = JournAIBrown,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Written at 9:30 PM",
                        style = AppTypography.bodyMedium,
                        color = JournAIBrown
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Journal text (from ViewModel)
                Text(
                    text = state.journalEntries[selectedDate] ?: "",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )

                Spacer(Modifier.height(16.dp))

                // Get AI Writing Tips Button
                JournButton(
                    text = "Get AI Writing Tips",
                    iconResId = R.drawable.ic_bulb, // Use your bulb icon resource
                    backgroundColor = JournAIPink,
                    contentColor = JournAIBrown,
                    onClick = onGetAiTips
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
    }
}
