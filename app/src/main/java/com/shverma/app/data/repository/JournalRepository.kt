package com.shverma.app.data.repository

import com.shverma.app.data.network.ApiService
import com.shverma.app.data.network.model.JournalByDateResponse
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.network.model.JournalEntryCreate
import com.shverma.app.data.network.model.WeeklyMoodSummaryResponse
import com.shverma.app.utils.Resource
import com.shverma.app.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject


interface JournalRepository {
    suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun getEntry(id: String): Resource<JournalDetail>
    suspend fun getHistory(): Resource<List<JournalDetail>>
    suspend fun updateEntry(id: String, entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun deleteEntry(id: String): Resource<Unit>
    suspend fun getEntriesByDate(date: OffsetDateTime): Resource<JournalByDateResponse>
    suspend fun getWeeklyMoodSummary(): Resource<WeeklyMoodSummaryResponse>
}


class JournalRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : JournalRepository {

    override suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.createJournalEntry(entry) }
        )
    }

    override suspend fun getEntry(id: String): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalEntry(id) }
        )
    }

    override suspend fun getHistory(): Resource<List<JournalDetail>> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalHistory() }
        )
    }

    override suspend fun updateEntry(
        id: String,
        entry: JournalEntryCreate
    ): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.updateJournalEntry(id, entry) }
        )
    }

    override suspend fun deleteEntry(id: String): Resource<Unit> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.deleteJournalEntry(id) }
        )
    }

    override suspend fun getEntriesByDate(date: OffsetDateTime): Resource<JournalByDateResponse> {
        val dateStr = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalEntriesByDate(dateStr) }
        )
    }

    override suspend fun getWeeklyMoodSummary(): Resource<WeeklyMoodSummaryResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getWeeklyMoodSummary() }
        )
    }
}
