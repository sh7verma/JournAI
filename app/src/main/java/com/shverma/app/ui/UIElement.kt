package com.shverma.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach
import com.shverma.app.ui.theme.JournAIYellow

@Composable
fun JournalCard(
    date: String,
    mood: String,
    contentPreview: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, JournAIBrown.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = date,
                    style = AppTypography.bodyMedium,
                    color = JournAIBrown
                )
                Text(
                    text = mood,
                    style = AppTypography.labelLarge,
                    color = JournAIBrown
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = contentPreview,
                style = AppTypography.bodyLarge,
                color = JournAIBrown,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun JournButton(
    text: String,
    @DrawableRes iconResId: Int? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = AppTypography.labelLarge)
    }
}

@Composable
fun PromptCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = JournAILightPeach
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = AppTypography.labelLarge,
                color = JournAIBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = AppTypography.bodyLarge,
                color = JournAIBrown
            )
        }
    }
}

@Composable
fun MoodSummaryCard(
    streakText: String,
    trendText: String,
    periodText: String = "Last 7 days",
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = JournAIYellow
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Mood Summary",
                style = AppTypography.titleMedium,
                color = JournAIBrown
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔥 $streakText",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )
                Text(
                    text = "↑ $trendText",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📅 $periodText",
                        style = AppTypography.bodyMedium,
                        color = JournAIBrown
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = JournAIBrown,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

