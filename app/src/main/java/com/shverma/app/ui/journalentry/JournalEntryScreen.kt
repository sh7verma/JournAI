package com.shverma.app.ui.journalentry

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shverma.androidstarter.R
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach

@Composable
fun JournalEntryScreen(
    modifier: Modifier = Modifier,
    journalEntryViewModel: JournalEntryViewModel = viewModel()
) {
    val uiState = journalEntryViewModel.uiState
    var text by remember { mutableStateOf(uiState.value.entryText) }
    var selectedMood by remember { mutableStateOf(uiState.value.selectedMood) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JournAIBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prompt Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "❞ Today's Prompt",
                    style = AppTypography.labelLarge,
                    color = JournAIBrown
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Reflect on your day and how you felt.",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )
            }
        }

        // Entry Text Field
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = JournAIBackground)
        ) {
            Box(Modifier.padding(20.dp)) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxSize(),
                    textStyle = AppTypography.bodyLarge.copy(color = JournAIBrown),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
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

        Spacer(modifier = Modifier.height(18.dp))

        // Mood Selector
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JournAILightPeach)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "How are you feeling?",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Mood.entries.forEach { mood ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedMood = mood }
                        ) {
                            Icon(
                                imageVector = mood.icon,
                                contentDescription = mood.label,
                                tint = if (selectedMood == mood) JournAIBrown else JournAIBrown.copy(
                                    alpha = 0.5f
                                ),
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = mood.label,
                                style = AppTypography.bodyMedium,
                                color = if (selectedMood == mood) JournAIBrown else JournAIBrown.copy(
                                    alpha = 0.5f
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Row: Edit, Save, Voice
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {}/*onEditClick*/,
                modifier = Modifier
                    .size(56.dp)
                    .background(JournAILightPeach, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_write), // Use your icon
                    contentDescription = "Edit",
                    tint = JournAIBrown,
                    modifier = Modifier.size(28.dp)
                )
            }

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                    }/*onSave(text, selectedMood!!)*/
                },
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JournAIBrown,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(54.dp)
                    .width(180.dp)
            ) {
                Text("Save Entry", style = AppTypography.labelLarge)
            }

            IconButton(
                onClick = {}/*onVoiceClick*/,
                modifier = Modifier
                    .size(56.dp)
                    .background(JournAILightPeach, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_record), // Use your icon
                    contentDescription = "Voice Entry",
                    tint = JournAIBrown,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
