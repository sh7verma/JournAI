package com.shverma.journai.data.repository

import com.shverma.journai.data.local.dao.JournalDao
import com.shverma.journai.data.local.entity.JournalEntity
import com.shverma.journai.data.network.model.JournalByDateResponse
import com.shverma.journai.data.network.model.JournalDetail
import com.shverma.journai.data.network.model.JournalEntryCreate
import com.shverma.journai.data.network.model.WeeklyMoodSummaryResponse
import com.shverma.journai.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.UUID
import javax.inject.Inject


interface JournalRepository {
    suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun getEntry(id: String): Resource<JournalDetail>
    suspend fun getHistory(): Resource<List<JournalDetail>>
    suspend fun updateEntry(id: String, entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun deleteEntry(id: String): Resource<Unit>
    suspend fun getEntriesByDate(date: OffsetDateTime): Resource<JournalByDateResponse>
    suspend fun getWeeklyMoodSummary(): Resource<WeeklyMoodSummaryResponse>
    suspend fun updateAiTip(id: String, aiTips: List<String>?): Resource<JournalDetail>
    suspend fun updateGrammarCorrection(id: String,  grammarCorrection: String?): Resource<JournalDetail>
}


class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
) : JournalRepository {

    override suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val now = OffsetDateTime.now()
                val journalEntity = JournalEntity(
                    id = UUID.randomUUID().toString(),
                    text = entry.text,
                    mood = entry.mood,
                    ai_sentiment = null,
                    aiTips = emptyList(),
                    grammarCorrection = null,
                    created_at = now,
                    updated_at = now
                )
                journalDao.insertJournalEntry(journalEntity)
                Resource.Success(journalEntity.toJournalDetail())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error creating journal entry")
            }
        }
    }

    override suspend fun getEntry(id: String): Resource<JournalDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val journalEntity = journalDao.getJournalEntryById(id)
                if (journalEntity != null) {
                    Resource.Success(journalEntity.toJournalDetail())
                } else {
                    Resource.Error("Journal entry not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error getting journal entry")
            }
        }
    }

    override suspend fun getHistory(): Resource<List<JournalDetail>> {
        return withContext(Dispatchers.IO) {
            try {
                val journalEntities = journalDao.getAllJournalEntries()
                val journalDetails = journalEntities.map { it.toJournalDetail() }
                Resource.Success(journalDetails)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error getting journal history")
            }
        }
    }

    override suspend fun updateEntry(
        id: String,
        entry: JournalEntryCreate
    ): Resource<JournalDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val existingEntry = journalDao.getJournalEntryById(id)
                if (existingEntry != null) {
                    val updatedEntry = existingEntry.copy(
                        text = entry.text,
                        mood = entry.mood,
                        updated_at = OffsetDateTime.now()
                    )
                    journalDao.updateJournalEntry(updatedEntry)
                    Resource.Success(updatedEntry.toJournalDetail())
                } else {
                    Resource.Error("Journal entry not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error updating journal entry")
            }
        }
    }

    override suspend fun deleteEntry(id: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                journalDao.deleteJournalEntryById(id)
                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error deleting journal entry")
            }
        }
    }

    override suspend fun getEntriesByDate(date: OffsetDateTime): Resource<JournalByDateResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val journalEntities = journalDao.getJournalEntriesByDate(date)
                val journalDetails = journalEntities.map { it.toJournalDetail() }
                val response = JournalByDateResponse(
                    entries = journalDetails,
                    startDate = date
                )
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error getting journal entries by date")
            }
        }
    }

    override suspend fun getWeeklyMoodSummary(): Resource<WeeklyMoodSummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val recentMoods = journalDao.getRecentMoods()
                val entriesCount = journalDao.getJournalEntriesCountLastWeek()

                // Simple algorithm to determine the dominant mood
                val moodMap = recentMoods.groupingBy { it }.eachCount()
                val dominantMood = moodMap.maxByOrNull { it.value }?.key

                val response = WeeklyMoodSummaryResponse(
                    mood = dominantMood,
                    streak = entriesCount
                )
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error getting weekly mood summary")
            }
        }
    }

    override suspend fun updateAiTip(
        id: String,
        aiTips: List<String>?
    ): Resource<JournalDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val existingEntry = journalDao.getJournalEntryById(id)
                if (existingEntry != null) {
                    val updatedEntry = existingEntry.copy(
                        aiTips = aiTips ?: existingEntry.aiTips,
                        updated_at = OffsetDateTime.now()
                    )
                    journalDao.updateJournalEntry(updatedEntry)
                    Resource.Success(updatedEntry.toJournalDetail())
                } else {
                    Resource.Error("Journal entry not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error updating AI enhancements")
            }
        }
    }

    override suspend fun updateGrammarCorrection(
        id: String,
        grammarCorrection: String?
    ): Resource<JournalDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val existingEntry = journalDao.getJournalEntryById(id)
                if (existingEntry != null) {
                    val updatedEntry = existingEntry.copy(
                        grammarCorrection = grammarCorrection ?: existingEntry.grammarCorrection,
                        updated_at = OffsetDateTime.now()
                    )
                    journalDao.updateJournalEntry(updatedEntry)
                    Resource.Success(updatedEntry.toJournalDetail())
                } else {
                    Resource.Error("Journal entry not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error updating AI enhancements")
            }
        }
    }
}
