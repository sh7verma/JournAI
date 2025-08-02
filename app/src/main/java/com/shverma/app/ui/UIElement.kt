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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shverma.androidstarter.R
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.ui.customViews.Mood
import com.shverma.app.ui.theme.AppTypography
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAILightPeach
import com.shverma.app.ui.theme.JournAIPink
import com.shverma.app.ui.theme.JournAIYellow
import com.shverma.app.utils.formatWrittenAt
import org.threeten.bp.OffsetDateTime

@Composable
fun DottedLineSeparator(
    modifier: Modifier = Modifier,
    color: Color = JournAIBrown.copy(alpha = 0.3f),
    strokeWidth: Float = 2f
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 4.dp)
            .drawBehind {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect,
                    strokeWidth = strokeWidth
                )
            }
    )
}

// Helper function to map mood string to Mood enum
private fun getMoodFromString(moodString: String): Mood? {
    return Mood.entries.find { it.label.equals(moodString, ignoreCase = true) }
}

@Composable
fun JournalCard(
    entry: JournalDetail,
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
            .clickable { onClick(entry.created_at) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = formatWrittenAt(entry.created_at),
                    style = AppTypography.bodyMedium,
                    color = JournAIBrown
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = mood?.icon ?: Icons.Outlined.SentimentSatisfied,
                        contentDescription = mood?.label ?: entry.mood,
                        tint = JournAIBrown,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = mood?.label ?: entry.mood.ifBlank { stringResource(R.string.default_mood) },
                        style = AppTypography.labelLarge,
                        color = JournAIBrown
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.text.ifBlank { stringResource(R.string.no_preview_available) },
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
                style = AppTypography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = JournAIBrown
            )
        }
    }
}

@Composable
fun MoodSummaryCard(
    streakText: String,
    mood: String? = null,
    onClick: () -> Unit = {}
) {
    // Calculate trend text from mood if trendText is not provided
    val displayTrend = when (mood) {
        null -> stringResource(R.string.no_mood_data)
        "Great" -> stringResource(R.string.mood_positive)
        "Good" -> stringResource(R.string.mood_neutral)
        else -> stringResource(R.string.mood_needs_improvement)
    }

    // Get mood icon if mood is provided
    val moodIcon = if (mood != null) {
        getMoodFromString(mood)?.icon ?: Icons.Outlined.SentimentSatisfied
    } else {
        Icons.Outlined.SentimentSatisfied
    }

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
                text = stringResource(R.string.weekly_mood_summary),
                style = AppTypography.titleMedium,
                color = JournAIBrown
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🔥 $streakText",
                style = AppTypography.bodyLarge,
                color = JournAIBrown
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = moodIcon,
                    contentDescription = mood ?: "",
                    tint = JournAIBrown,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "↑ $displayTrend",
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown
                )
            }
        }
    }
}

@Composable
fun DetailedJournalCard(
    entry: JournalDetail,
    tips: List<String>? = null,
    grammarCorrection: String? = null,
    onGetAiTips: (() -> Unit)? = null,
    onGetGrammarCorrection: (() -> Unit)? = null
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
                    contentDescription = mood?.label ?: "",
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
                    text = formatWrittenAt(entry.created_at),
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

            Spacer(Modifier.height(16.dp))

            if (!tips.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                // Custom dotted line separator
                DottedLineSeparator()
                Spacer(Modifier.height(8.dp))
            }

            // Display tips in italics if available
            if (!tips.isNullOrEmpty()) {
                Column {
                    Text(
                        text = stringResource(R.string.writing_tips),
                        style = AppTypography.labelLarge,
                        color = JournAIBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    tips.forEach { tip ->
                        Text(
                            text = "• $tip",
                            style = AppTypography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = JournAIBrown
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            // Add dotted line between tips and grammar correction
            if (!grammarCorrection.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                // Custom dotted line separator
                DottedLineSeparator()
                Spacer(Modifier.height(8.dp))
            }

            // Display grammar correction if available
            if (!grammarCorrection.isNullOrEmpty()) {
                Spacer(Modifier.height(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.grammar_correction),
                        style = AppTypography.labelLarge,
                        color = JournAIBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = grammarCorrection,
                        style = AppTypography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = JournAIBrown
                    )
                }
            }

            // Button for AI Writing Tips
            if (onGetAiTips != null && tips.isNullOrEmpty()) {
                Spacer(Modifier.height(16.dp))
                JournButton(
                    text = stringResource(R.string.get_ai_writing_tips),
                    iconResId = com.shverma.androidstarter.R.drawable.ic_bulb,
                    backgroundColor = JournAIPink,
                    contentColor = JournAIBrown,
                    onClick = onGetAiTips
                )
            }

            // Button for Grammar Correction
            if (!tips.isNullOrEmpty() && onGetGrammarCorrection != null && grammarCorrection.isNullOrEmpty()) {
                Spacer(Modifier.height(16.dp))
                JournButton(
                    text = stringResource(R.string.get_grammar_correction),
                    iconResId = com.shverma.androidstarter.R.drawable.ic_bulb,
                    backgroundColor = JournAIYellow,
                    contentColor = JournAIBrown,
                    onClick = onGetGrammarCorrection
                )
            }
        }
    }
}
