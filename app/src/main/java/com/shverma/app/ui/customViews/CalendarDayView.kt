package com.shverma.app.ui.customViews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAIPink
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

// ----- Data Model -----
data class CalendarDayUi(
    val date: OffsetDateTime,
    val moodIcon: ImageVector?,
    val moodLabel: String
)

enum class Mood(val label: String, val icon: ImageVector) {
    Sad("Sad", Icons.Outlined.SentimentVeryDissatisfied),
    Down("Down", Icons.Outlined.SentimentDissatisfied),
    Good("Good", Icons.Outlined.SentimentSatisfied),
    Great("Great", Icons.Outlined.SentimentVerySatisfied)
}

// ----- Helper Function -----
fun generateCalendarDays(
    startDate: OffsetDateTime,
    endDate: OffsetDateTime,
    moodMap: Map<OffsetDateTime, Mood?>
): List<CalendarDayUi> {
    val days = mutableListOf<CalendarDayUi>()
    var currentDate = startDate.toLocalDate()
    val endDateLocal = endDate.toLocalDate()
    val offset = startDate.offset

    while (!currentDate.isAfter(endDateLocal)) {
        val dateTime = currentDate.atStartOfDay().atOffset(offset)
        // Try exact match first, fallback to date-only match for mood
        val mood = moodMap[dateTime] ?: moodMap.entries.find { it.key.toLocalDate() == currentDate }?.value
        days.add(
            CalendarDayUi(
                date = dateTime,
                moodIcon = mood?.icon,
                moodLabel = mood?.label ?: ""
            )
        )
        currentDate = currentDate.plusDays(1)
    }
    return days
}

// Overloaded function for backward compatibility
fun generateCalendarDays(
    startDate: LocalDate,
    endDate: LocalDate,
    moodMap: Map<LocalDate, Mood?>
): List<CalendarDayUi> {
    // Convert LocalDate to OffsetDateTime
    val startDateOffset = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).atOffset(ZoneOffset.UTC)
    val endDateOffset = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).atOffset(ZoneOffset.UTC)

    // Convert mood map keys from LocalDate to OffsetDateTime
    val offsetMoodMap = moodMap.mapKeys { (localDate, _) ->
        localDate.atStartOfDay().toInstant(ZoneOffset.UTC).atOffset(ZoneOffset.UTC)
    }

    return generateCalendarDays(startDateOffset, endDateOffset, offsetMoodMap)
}

// ----- The Calendar Row Composable -----
@Composable
fun CalendarWeekRow(
    days: List<CalendarDayUi>,
    selectedDate: OffsetDateTime,
    specialDate: OffsetDateTime? = null,
    modifier: Modifier = Modifier,
    onDateSelected: (OffsetDateTime) -> Unit
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(horizontal = 8.dp)
            .fillMaxSize()
    ) {
        days.forEach { day ->
            val isSelected = day.date.toLocalDate() == selectedDate.toLocalDate()
            val isSpecial = specialDate != null && day.date.toLocalDate() == specialDate.toLocalDate()
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .width(70.dp)
                        .height(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> JournAIPink
                                else -> Color.White
                            }
                        )
                        .clickable { onDateSelected(day.date) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.date.dayOfWeek.name.take(3),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = JournAIBrown
                    )
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = JournAIBrown
                    )
                    Spacer(Modifier.height(2.dp))
                    Icon(
                        imageVector = day.moodIcon ?: Icons.Outlined.SentimentSatisfied,
                        contentDescription = day.moodLabel.ifEmpty { "Neutral" },
                        tint = JournAIBrown,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // Pink ring for "special" day, but not main selected
                if (isSpecial && !isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 36.dp)
                            .size(30.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFEB43AD), // Your ring color
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

fun createMoodMap(startDate: OffsetDateTime, endDate: OffsetDateTime): Map<OffsetDateTime, Mood> {
    val moods = listOf(Mood.Great, Mood.Good, Mood.Sad, Mood.Down)
    val daysCount = startDate.toLocalDate().toEpochDay() - endDate.toLocalDate().toEpochDay() + 1
    return (0 until daysCount.toInt()).associate { i ->
        val date = startDate.minusDays(i.toLong())
        date to moods[i % moods.size]
    }
}

// Overloaded function for backward compatibility
fun createMoodMap(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Mood> {
    val moods = listOf(Mood.Great, Mood.Good, Mood.Sad, Mood.Down)
    val daysCount = startDate.toEpochDay() - endDate.toEpochDay() + 1
    return (0 until daysCount.toInt()).associate { i ->
        val date = startDate.minusDays(i.toLong())
        date to moods[i % moods.size]
    }
}
// ----- Preview -----
@Composable
@Preview(showBackground = true)
fun CalendarWeekRowPreview() {
    val startDate = OffsetDateTime.of(2024, 2, 25, 0, 0, 0, 0, ZoneOffset.UTC)
    val endDate = OffsetDateTime.of(2024, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC)
    val moodMap = mapOf(
        OffsetDateTime.of(2024, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC) to Mood.Great,
        OffsetDateTime.of(2024, 2, 27, 0, 0, 0, 0, ZoneOffset.UTC) to Mood.Good,
        OffsetDateTime.of(2024, 2, 26, 0, 0, 0, 0, ZoneOffset.UTC) to Mood.Sad
    )
    val days = generateCalendarDays(startDate, endDate, moodMap)

    var selectedDate by remember { mutableStateOf(OffsetDateTime.of(2024, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC)) }
    var specialDate by remember { mutableStateOf(OffsetDateTime.of(2024, 2, 26, 0, 0, 0, 0, ZoneOffset.UTC)) } // ring on 26

    Column(Modifier.background(Color(0xFFFFF8F6))) {
        CalendarWeekRow(
            days = days,
            selectedDate = selectedDate,
            specialDate = specialDate,
            onDateSelected = { selectedDate = it }
        )
    }
}
