package com.shverma.app.data.network.model

data class JournalEntryCreate(
    val text: String,
    val mood: String
)

data class JournalDetail(
    val id: String,
    val text: String,
    val mood: String,
    val date: String,
    val ai_sentiment: String?,
    val created_at: String,
    val updated_at: String
)

data class JournalByDateResponse(
    val entries: List<JournalDetail>,
    val startDate: String?
)
