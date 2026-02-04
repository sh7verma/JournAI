package com.shverma.journai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.shverma.journai.data.local.converter.DateTimeConverter
import com.shverma.journai.data.local.converter.StringListConverter
import com.shverma.journai.data.network.model.JournalDetail
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "journal_entries")
@TypeConverters(DateTimeConverter::class, StringListConverter::class)
data class JournalEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val mood: String,
    val ai_sentiment: String?,
    val aiTips: List<String>,
    val grammarCorrection: String?,
    val created_at: OffsetDateTime,
    val updated_at: OffsetDateTime
) {
    fun toJournalDetail(): JournalDetail {
        return JournalDetail(
            id = id,
            text = text,
            mood = mood,
            ai_sentiment = ai_sentiment,
            aiTips = aiTips,
            grammarCorrection = grammarCorrection,
            created_at = created_at,
            updated_at = updated_at
        )
    }

    companion object {
        fun fromJournalDetail(journalDetail: JournalDetail): JournalEntity {
            return JournalEntity(
                id = journalDetail.id,
                text = journalDetail.text,
                mood = journalDetail.mood,
                ai_sentiment = journalDetail.ai_sentiment,
                aiTips = journalDetail.aiTips,
                grammarCorrection = journalDetail.grammarCorrection,
                created_at = journalDetail.created_at,
                updated_at = journalDetail.updated_at
            )
        }
    }
}