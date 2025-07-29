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
import androidx.compose.material.icons.outlined.SentimentSatisfied
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach
import com.shverma.app.ui.theme.JournAIPink
import com.shverma.app.ui.theme.JournAIYellow
import com.shverma.app.utils.formatWrittenAt
import org.threeten.bp.OffsetDateTime

// Helper function to map mood string to Mood enum
private fun getMoodFromString(moodString: String): Mood? {
    return Mood.entries.find { it.label.equals(moodString, ignoreCase = true) }
}

@Composable
fun JournalCard(
    entry: JournalDetail,
    moodIcon: ImageVector? = null,
    onClick: (OffsetDateTime) -> Unit = {}
) {
    // Try to map the mood string to a Mood enum
    val mood = getMoodFromString(entry.mood)

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, JournAIBrown.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(entry.date) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = formatWrittenAt(entry.date),
                    style = AppTypography.bodyMedium,
                    color = JournAIBrown
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = mood?.icon ?: moodIcon ?: Icons.Outlined.SentimentSatisfied,
                        contentDescription = mood?.label ?: entry.mood,
                        tint = JournAIBrown,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = mood?.label ?: entry.mood.ifBlank { "Okay" },
                        style = AppTypography.labelLarge,
                        color = JournAIBrown
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.text.ifBlank { "No preview available." },
                style = AppTypography.bodyLarge,
                color = JournAIBrown,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Keeping the old function for backward compatibility
@Composable
fun JournalCard(
    date: String,
    mood: String,
    contentPreview: String,
    onClick: (String) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, JournAIBrown.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(date) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = date,
                    style = AppTypography.bodyMedium,
                    color = JournAIBrown
                )
                Text(
                    text = mood.ifBlank { "Okay" },
                    style = AppTypography.labelLarge,
                    color = JournAIBrown
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = contentPreview.ifBlank { "No preview available." },
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
            .height(54.dp), // matches your Figma
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp) // matches your design
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

@Composable
fun DetailedJournalCard(
    entry: JournalDetail,
    writtenAt: String? = null,
    onGetAiTips: (() -> Unit)? = null
) {
    // Try to map the mood string to a Mood enum
    val mood = getMoodFromString(entry.mood)

    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = mood?.icon ?: Icons.Outlined.SentimentSatisfied,
                    contentDescription = mood?.label  ?: "",
                    tint = JournAIBrown,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = mood?.label ?: entry.mood,
                    style = AppTypography.labelLarge,
                    color = JournAIBrown,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = writtenAt ?: formatWrittenAt(entry.created_at),
                    style = AppTypography.bodyMedium,
                    color = JournAIBrown
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = entry.text,
                style = AppTypography.bodyLarge,
                color = JournAIBrown
            )

            if (onGetAiTips != null) {
                Spacer(Modifier.height(16.dp))
                JournButton(
                    text = "Get AI Writing Tips",
                    iconResId = com.shverma.androidstarter.R.drawable.ic_bulb,
                    backgroundColor = JournAIPink,
                    contentColor = JournAIBrown,
                    onClick = onGetAiTips
                )
            }
        }
    }
}
