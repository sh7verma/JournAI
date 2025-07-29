package com.shverma.app.data.network.model

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
    val created_at: OffsetDateTime,
    val updated_at: OffsetDateTime
)

data class JournalByDateResponse(
    val entries: List<JournalDetail>,
    val startDate: OffsetDateTime?
)
