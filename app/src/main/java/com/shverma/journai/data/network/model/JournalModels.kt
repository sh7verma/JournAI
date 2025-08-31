package com.shverma.journai.data.network.model

import org.threeten.bp.OffsetDateTime

data class JournalEntryCreate(
    val text: String,
    val mood: String
)

data class JournalDetail(
    val id: String,
    val text: String,
    val mood: String,
    val ai_sentiment: String?,
    val aiTips: List<String> = emptyList(),
    val grammarCorrection: String? = null,
    val created_at: OffsetDateTime,
    val updated_at: OffsetDateTime
)

data class JournalByDateResponse(
    val entries: List<JournalDetail>,
    val startDate: OffsetDateTime?
)

data class WeeklyMoodSummaryResponse(
    val mood: String? = null,
    val streak: Int? = 0
)
