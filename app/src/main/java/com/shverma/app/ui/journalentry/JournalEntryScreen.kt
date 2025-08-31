package com.shverma.app.ui.journalentry

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shverma.app.R
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach
import com.shverma.app.ui.theme.dimensions
import com.shverma.app.utils.UiEvent
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun JournalEntryScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    onBackClick: (() -> Unit),
    journalEntryViewModel: JournalEntryViewModel = hiltViewModel<JournalEntryViewModel>()
) {

    val uiState = journalEntryViewModel.uiState.collectAsState()
    val state = uiState.value

    LaunchedEffect(true) {
        journalEntryViewModel.uiEvent.receiveAsFlow().collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                is UiEvent.NavigateUp<*> -> {
                    onBackClick()
                }
            }
        }
    }

    val dims = dimensions()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JournAIBackground)
            .padding(dims.spacingRegular),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prompt Card
        Card(
            shape = RoundedCornerShape(dims.radiusLarge),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dims.spacingLarge),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(dims.spacingRegular)) {
                Text(
                    text = stringResource(R.string.todays_prompt_quote),
                    style = AppTypography.labelLarge,
                    color = JournAIBrown
                )
                Spacer(modifier = Modifier.height(dims.spacingSmall))
                Text(
                    text = state.prompt,
                    style = AppTypography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = JournAIBrown
                )
            }
        }

        // Entry Text Field
        Card(
            shape = RoundedCornerShape(dims.radiusLarge),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = JournAIBackground)
        ) {
            Box(Modifier.padding(dims.spacingLarge)) {
                BasicTextField(
                    value = state.entryText,
                    onValueChange = { journalEntryViewModel.onTextChange(it) },
                    modifier = Modifier
                        .fillMaxSize(),
                    textStyle = AppTypography.bodyLarge.copy(color = JournAIBrown),
                    decorationBox = { innerTextField ->
                        if (state.entryText.isEmpty()) {
                            Text(
                                text = "Start writing here...",
                                style = AppTypography.bodyLarge.copy(color = JournAIBrown.copy(alpha = 0.5f))
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(dims.spacingRegular))

        // Mood Selector
        Card(
            shape = RoundedCornerShape(dims.radiusMedium),
            elevation = CardDefaults.cardElevation(defaultElevation = dims.elevationLarge),
            colors = CardDefaults.cardColors(containerColor = JournAILightPeach)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(dims.spacingRegular)
            ) {
                // Title Row with Analyze Button
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "How are you feeling?",
                        style = AppTypography.titleMedium,
                        color = JournAIBrown,
                        modifier = Modifier.weight(1f)
                    )

                    if (!state.isAnalyzingSentiment) {
                        Button(
                            onClick = { journalEntryViewModel.analyzeMoodInEntry() },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JournAIBrown,
                                contentColor = JournAILightPeach
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_bulb),
                                contentDescription = "Analyze Mood",
                                tint = JournAILightPeach,
                                modifier = Modifier.size(dims.iconSizeMedium)
                            )
                        }
                    }
                }

                // Mood Selector Row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Mood.entries.forEach { mood ->
                        // Add animation for selection
                        val selected = state.selectedMood == mood
                        val bgColor by animateColorAsState(
                            if (selected) JournAIBrown.copy(alpha = 0.12f) else Color.Transparent,
                            label = "mood-bg"
                        )
                        val iconColor by animateColorAsState(
                            if (selected) JournAIBrown else JournAIBrown.copy(alpha = 0.5f),
                            label = "mood-icon"
                        )
                        val textColor =
                            if (selected) JournAIBrown else JournAIBrown.copy(alpha = 0.5f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { journalEntryViewModel.onMoodSelected(mood) }
                                .padding(dims.spacingSmall)
                        ) {
                            Icon(
                                imageVector = mood.icon,
                                contentDescription = mood.label,
                                tint = iconColor,
                                modifier = Modifier.size(dims.iconSizeLarge + dims.iconSizeSmall)
                            )
                            Spacer(Modifier.height(dims.spacingXXSmall))
                            Text(
                                text = mood.label,
                                style = AppTypography.bodySmall,
                                color = textColor
                            )
                        }
                    }
                }

                // Mood analysis card (if available)
                if (state.sentimentAnalysis != null) {
                    Spacer(modifier = Modifier.height(dims.spacingXXLarge))
                    Card(
                        shape = RoundedCornerShape(dims.radiusLarge),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = dims.elevationMedium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(dims.spacingRegular)) {
                            Text(
                                text = "Mood Analysis",
                                style = AppTypography.labelLarge,
                                color = JournAIBrown
                            )
                            Spacer(Modifier.height(dims.spacingSmall))
                            Text(
                                text = state.sentimentAnalysis,
                                style = AppTypography.bodyMedium.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = JournAIBrown
                            )
                        }
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(dims.spacingLarge))

        // Bottom Row: Edit, Save, Voice
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            /* IconButton(
                 onClick = {}*//*onEditClick*//*,
                modifier = Modifier
                    .size(dims.buttonHeight)
                    .background(JournAILightPeach, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_write), // Use your icon
                    contentDescription = "Edit",
                    tint = JournAIBrown,
                    modifier = Modifier.size(dims.iconSizeLarge + dims.spacingXXSmall)
                )
            }*/

            Button(
                onClick = { journalEntryViewModel.createJournalEntry() },
                shape = RoundedCornerShape(dims.radiusXLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JournAIBrown,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(dims.buttonHeight)
                    .width(180.dp)
            ) {
                Text("Save Entry", style = AppTypography.labelLarge)
            }

            /* IconButton(
                 onClick = {}*//*onVoiceClick*//*,
                modifier = Modifier
                    .size(dims.buttonHeight)
                    .background(JournAILightPeach, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_record), // Use your icon
                    contentDescription = "Voice Entry",
                    tint = JournAIBrown,
                    modifier = Modifier.size(dims.iconSizeLarge + dims.spacingXXSmall)
                )
            }*/
        }

        Spacer(modifier = Modifier.height(dims.spacingSmall))
    }
}
